package com.panera.cmt.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.panera.cmt.entity.Iso3Response;
import com.panera.cmt.enums.Role;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.mongo.repository.IAuthenticatedUserRepository;
import com.panera.cmt.service.app_config.IAuthGroupsService;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.panera.cmt.config.Constants.APP_CONFIG_AUTH_GROUP;
import static com.panera.cmt.config.Constants.getExpirationDate;
import static com.panera.cmt.util.SharedUtils.*;
import static java.util.Collections.singletonMap;

@Service
@Slf4j
public class AuthenticationService implements IAuthenticationService {

    @Value("${iso3.auth}")
    private String auth;

    @Value("${iso3.base-url}")
    private String baseUrl;

    private IAuthenticatedUserRepository authenticatedUserRepository;
    private IAuthGroupsService authGroupsService;

    @Autowired
    public AuthenticationService(IAuthenticatedUserRepository authenticatedUserRepository, IAuthGroupsService authGroupsService) {
        this.authenticatedUserRepository = authenticatedUserRepository;
        this.authGroupsService = authGroupsService;
    }

    @Override
    public Optional<AuthenticatedUser> getAuthenticatedUser() {
        AuthenticatedUser authUser = AuthenticatedUserManager.getAuthenticatedUser();

        if (authUser != null) {
            return getAuthenticatedUser(authUser.getAccessToken());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<AuthenticatedUser> getAuthenticatedUser(String accessToken) {
        return authenticatedUserRepository.getByAccessToken(accessToken);
    }

    @Override
    public Optional<AuthenticatedUser> login(String username, String password) {
        StopWatch stopWatch = new StopWatch(log, "authenticate", "Authenticating user=" + username);

        if (isNull(username, password)) {
            stopWatch.checkPoint("Cannot authenticate, something is null");
            return Optional.empty();
        }

        Iso3Response iso3Response = getFromISO3(username, password, stopWatch);

        if (iso3Response != null && iso3Response.getId_token() != null) {
            // Decode the token
            JsonObject decodedJWT = decodeJWT(iso3Response.getId_token());

            stopWatch.checkPoint(String.format("Response from server decodedBody=%s", decodedJWT));

            AuthenticatedUser authUser = new AuthenticatedUser();
            authUser.setUsername(username);
            authUser.setAccessToken(createGUID());
            authUser.setLoginDate(new Date());
            authUser.setExpirationDate(getExpirationDate());

            // Check if there is any roles (AD groups)
            if (decodedJWT.get("role") != null) {
                String decodedJWTRoles = null;

                if (decodedJWT.get("role").isJsonArray()) {
                    decodedJWTRoles = decodedJWT.get("role").getAsJsonArray().toString().replace("\"", "");
                } else {
                    decodedJWTRoles = decodedJWT.get("role").getAsString().replace("\"", "");
                }

                if (checkGroups(decodedJWTRoles)) {
                    authUser.setRole(getUserRole(decodedJWTRoles));

                    if (authUser.getRole() != null) {
                        stopWatch.checkPoint(String.format("User=%s is authenticated, setting role=%s, accessToken=%s", username, authUser.getRole().name(), authUser.getAccessToken()));

                        // Check if there is a name
                        if (decodedJWT.get("firstname") != null) {
                            authUser.setFirstName(decodedJWT.get("firstname").getAsString().replace("\"", ""));
                        }
                        if (decodedJWT.get("lastname") != null) {
                            authUser.setLastName(decodedJWT.get("lastname").getAsString().replace("\"", ""));
                        }
                        if (decodedJWT.get("displayname") != null) {
                            authUser.setDisplayName(decodedJWT.get("displayname").getAsString().replace("\"", ""));
                        }
                        if (decodedJWT.get("knownas") != null) {
                            authUser.setKnownAs(decodedJWT.get("knownas").getAsString().replace("\"", ""));
                        }

                        // Check if there is an email
                        if (decodedJWT.get("emailaddress") != null) {
                            authUser.setEmailAddress(decodedJWT.get("emailaddress").getAsString().replace("\"", ""));
                        }

                        // Delete any active session
                        authenticatedUserRepository.getByUsername(username)
                                .map(activeSession -> {
                                    authenticatedUserRepository.delete(activeSession);
                                    return activeSession;
                                });

                        authenticatedUserRepository.save(authUser);

                        return Optional.of(authUser);
                    } else {
                        stopWatch.checkPoint(String.format("Error getting user=%s role, groups=%s", username, decodedJWT.get("role").getAsString().replace("\"", "")));
                    }
                } else {
                    stopWatch.checkPoint(String.format("User=%s does not belong to any valid groups, groups=%s", username, decodedJWT.get("role").getAsString().replace("\"", "")));
                }
            } else {
                stopWatch.checkPoint(String.format("User=%s does not belong to any groups", username));
                stopWatch.checkPoint("User does not belong to any groups");
            }
        } else {
            stopWatch.checkPoint(String.format("Username/password is incorrect, username=%s", username));
        }

        return Optional.empty();
    }

    @Override
    public void logout() {
        AuthenticatedUser authUser = AuthenticatedUserManager.getAuthenticatedUser();

        if (authUser != null) {
            authenticatedUserRepository.delete(authUser);
        }
    }

    @Override
    @Transactional
    public void updateSession(String sessionId, AuthenticatedUser updatedAuthenticatedUser) {
        if (isNull(sessionId, updatedAuthenticatedUser)) {
            return;
        }

        if (authenticatedUserRepository.getById(sessionId).isPresent()) {
            updatedAuthenticatedUser.setId(sessionId);

            authenticatedUserRepository.save(updatedAuthenticatedUser);
        }
    }

    private JsonObject decodeJWT(String encodedString) {
        // Decode the JWT
        String[] splitString = encodedString.split("\\.");
        String base64EncodedBody = splitString[1];

        // Decode the body
        Base64 base64Url = new Base64(true);
        String body = new String(base64Url.decode(base64EncodedBody));

        // Convert to JSONObject and return
        return new JsonParser().parse(body).getAsJsonObject();
    }

    private Iso3Response getFromISO3(String username, String password, StopWatch stopWatch) {
        WebClient client = WebClient
                .builder()
                .baseUrl(baseUrl)
                .filter((request, next) -> {
                    stopWatch.checkPoint(String.format("Request to server method=%s, url=%s, body=%s", request.method().toString().toUpperCase(), request.url().toString(), String.format("{\"username\":\"%s\",\"password\":\"******\"}", username)));

                    return next.exchange(request);
                })
                .defaultUriVariables(singletonMap("url", baseUrl))
                .build();

        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("username", username);
        map.add("password", password);

        Iso3Response response = client.post()
                .uri("/token?scope=openid panera")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(map))
                .header(HttpHeaders.AUTHORIZATION, "Basic " + auth)
                .exchange()
                .doOnSuccessOrError((clientResponse, throwable) -> stopWatch.checkPoint(String.format("Response from server status=%d", clientResponse.statusCode().value())))
                .flatMap(clientResponse -> {
                    if (clientResponse.statusCode().is4xxClientError()) {
                        return Mono.empty();
                    } else if (clientResponse.statusCode().is5xxServerError()) {
                        return Mono.error(new RuntimeException());
                    } else {
                        return clientResponse.bodyToMono(Iso3Response.class);
                    }
                }).block();

        if (response != null) {
            stopWatch.checkPoint(String.format("Response from server body=%s", new Gson().toJson(response)));
        }

        return response;
    }

    /**
     * Method that checks the given array of groups in the response against the provided authorized groups
     *
     * @param groups   The authorized array of groups
     * @param response The groups that this user belongs to
     * @return The status of if this person belongs to the requested permission group
     */
    private boolean checkGroup(List<String> groups, String response) {
        if (isAnyNull(groups, response) || groups.size() == 0) {
            return false;
        }

        for (String group : groups) {
            if (response.toLowerCase().replaceAll("\\s+", "").contains(group.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method that checks the returned groups against the allowed groups in the settings
     *
     * @param response The string containing the groups that the user belongs to
     * @return The boolean response to the question
     */
    private boolean checkGroups(String response) {
        for (String group : authGroupsService.getAllAuthGroups()) {
            if (response.toLowerCase().replaceAll("\\s+", "").contains(group.toLowerCase())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Method that gets the user's role
     *
     * @param response The data that was returned from the server
     * @return The user's role
     */
    private Role getUserRole(String response) {
        Role role = null;

        Map<String, List<String>> authGroups = authGroupsService.getAuthGroups();

        if (checkGroup(authGroups.get(APP_CONFIG_AUTH_GROUP + ".admin"), response)) {
            role = setRole(role, Role.ADMIN);
        }
        if (checkGroup(authGroups.get(APP_CONFIG_AUTH_GROUP + ".cbss"), response)) {
            role = setRole(role, Role.CBSS);
        }
        if (checkGroup(authGroups.get(APP_CONFIG_AUTH_GROUP + ".cbss_manager"), response)) {
            role = setRole(role, Role.CBSS_MANAGER);
        }
        if (checkGroup(authGroups.get(APP_CONFIG_AUTH_GROUP + ".prod_support"), response)) {
            role = setRole(role, Role.PROD_SUPPORT);
        }
        if (checkGroup(authGroups.get(APP_CONFIG_AUTH_GROUP + ".read_only"), response)) {
            role = setRole(role, Role.READ_ONLY);
        }
        if (checkGroup(authGroups.get(APP_CONFIG_AUTH_GROUP + ".sales_admin"), response)) {
            role = setRole(role, Role.SALES_ADMIN);
        }
        if (checkGroup(authGroups.get(APP_CONFIG_AUTH_GROUP + ".security"), response)) {
            role = setRole(role, Role.SECURITY);
        }
        return role;
    }

    private Role setRole(Role current, Role toEvaluate) {
        if (current == null) {
            return toEvaluate;
        }
        if (current.equals(Role.ADMIN) || toEvaluate.equals(Role.ADMIN)) {
            return Role.ADMIN;
        }
        if (current.equals(Role.PROD_SUPPORT) || toEvaluate.equals(Role.PROD_SUPPORT)) {
            return Role.PROD_SUPPORT;
        }
        if (current.equals(Role.CBSS_MANAGER) || toEvaluate.equals(Role.CBSS_MANAGER)) {
            return Role.CBSS_MANAGER;
        }
        if (current.equals(Role.CBSS) || toEvaluate.equals(Role.CBSS)) {
            return Role.CBSS;
        }
        if (current.equals(Role.SALES_ADMIN) || toEvaluate.equals(Role.SALES_ADMIN)) {
            return Role.SALES_ADMIN;
        }
        return null;
    }

}