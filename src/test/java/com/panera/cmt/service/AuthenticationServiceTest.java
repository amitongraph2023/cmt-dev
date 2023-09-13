package com.panera.cmt.service;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.entity.Iso3Response;
import com.panera.cmt.enums.Role;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.mongo.repository.IAuthenticatedUserRepository;
import com.panera.cmt.service.app_config.IAuthGroupsService;
import com.panera.cmt.test_builders.AuthenticatedUserBuilder;
import com.panera.cmt.test_builders.Iso3ResponseBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.config.Constants.APP_CONFIG_AUTH_GROUP;
import static com.panera.cmt.test_util.SharedTestUtil.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SuppressWarnings("Duplicates")
public class AuthenticationServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Mock
    private IAuthenticatedUserRepository authenticatedUserRepository;

    @Mock
    private IAuthGroupsService authGroupsService;

    @InjectMocks
    private AuthenticationService classUnderTest;

    private AuthenticatedUser authUser = new AuthenticatedUserBuilder().build();

    @Before
    public void setUp() {
        AuthenticatedUserManager.setAuthenticatedUser(authUser);
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(classUnderTest, "auth", UUID.randomUUID().toString());
        ReflectionTestUtils.setField(classUnderTest, "baseUrl", "http://localhost:" + wireMockRule.port());
    }

    @Test
    public void getAuthenticatedUser_WithoutAccessToken_AuthenticatedUserIsSet_AuthenticatedUserIsFound_ExpectOptionalOfAuthenticatedUser() {
        when(authenticatedUserRepository.getByAccessToken(authUser.getAccessToken())).thenReturn(Optional.of(authUser));

        Optional<AuthenticatedUser> result = classUnderTest.getAuthenticatedUser();

        verify(authenticatedUserRepository, times(1)).getByAccessToken(anyString());

        assertTrue(result.isPresent());
    }
    @Test
    public void getAuthenticatedUser_WithoutAccessToken_AuthenticatedUserIsNotSet_ExpectEmptyOptional() {
        AuthenticatedUserManager.setAuthenticatedUser(null);

        when(authenticatedUserRepository.getByAccessToken(authUser.getAccessToken())).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.getAuthenticatedUser();

        verify(authenticatedUserRepository, times(0)).getByAccessToken(anyString());

        assertFalse(result.isPresent());
    }

    @Test
    public void getAuthenticatedUser_AuthenticatedUserIsFound_ExpectOptionalOfAuthenticatedUser() {
        String accessToken = UUID.randomUUID().toString();
        AuthenticatedUser authUser = new AuthenticatedUserBuilder().build();

        when(authenticatedUserRepository.getByAccessToken(accessToken)).thenReturn(Optional.of(authUser));

        Optional<AuthenticatedUser> result = classUnderTest.getAuthenticatedUser(accessToken);

        verify(authenticatedUserRepository, times(1)).getByAccessToken(anyString());

        assertTrue(result.isPresent());
    }
    @Test
    public void getAuthenticatedUser_AuthenticatedUserIsNotFound_ExpectEmptyOptional() {
        String accessToken = UUID.randomUUID().toString();

        when(authenticatedUserRepository.getByAccessToken(accessToken)).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.getAuthenticatedUser(accessToken);

        verify(authenticatedUserRepository, times(1)).getByAccessToken(anyString());

        assertFalse(result.isPresent());
    }

    @Test
    public void login_LoginIsSuccessful_ExpectOptionalOfAuthorizedUser() {
        String authGroup = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String firstName = UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        Role role = Role.ADMIN;
        String username = UUID.randomUUID().toString();

        Iso3Response iso3Response = new Iso3ResponseBuilder().withId_token(buildIso3ResponseMap(emailAddress, firstName, lastName, authGroup)).build();

        stubFor(post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(iso3Response))));

        when(authGroupsService.getAllAuthGroups()).thenReturn(singletonList(authGroup));
        when(authGroupsService.getAuthGroups()).thenReturn(buildAuthGroups(role, authGroup));
        when(authenticatedUserRepository.getByUsername(username)).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.login(username, UUID.randomUUID().toString());

        WireMock.verify(postRequestedFor(urlEqualTo("/token?scope=openid%20panera")));
        verify(authGroupsService, times(1)).getAllAuthGroups();
        verify(authGroupsService, times(1)).getAuthGroups();
        verify(authenticatedUserRepository, times(1)).getByUsername(anyString());
        verify(authenticatedUserRepository, times(0)).delete(any(AuthenticatedUser.class));
        verify(authenticatedUserRepository, times(1)).save(any(AuthenticatedUser.class));

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        assertEquals(role, result.get().getRole());
        assertEquals(firstName, result.get().getFirstName());
        assertEquals(lastName, result.get().getLastName());
        assertEquals(emailAddress, result.get().getEmailAddress());
    }
    @Test
    public void login_LoginIsSuccessful_ExistingSessionExists_ExpectOptionalOfAuthorizedUser() {
        String authGroup = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String firstName = UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        Role role = Role.ADMIN;
        String username = UUID.randomUUID().toString();

        Iso3Response iso3Response = new Iso3ResponseBuilder().withId_token(buildIso3ResponseMap(emailAddress, firstName, lastName, authGroup)).build();

        stubFor(post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(iso3Response))));

        when(authGroupsService.getAllAuthGroups()).thenReturn(singletonList(authGroup));
        when(authGroupsService.getAuthGroups()).thenReturn(buildAuthGroups(role, authGroup));
        when(authenticatedUserRepository.getByUsername(username)).thenReturn(Optional.of(new AuthenticatedUserBuilder().build()));

        Optional<AuthenticatedUser> result = classUnderTest.login(username, UUID.randomUUID().toString());

        WireMock.verify(postRequestedFor(urlEqualTo("/token?scope=openid%20panera")));
        verify(authGroupsService, times(1)).getAllAuthGroups();
        verify(authGroupsService, times(1)).getAuthGroups();
        verify(authenticatedUserRepository, times(1)).getByUsername(anyString());
        verify(authenticatedUserRepository, times(1)).delete(any(AuthenticatedUser.class));
        verify(authenticatedUserRepository, times(1)).save(any(AuthenticatedUser.class));

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        assertEquals(role, result.get().getRole());
        assertEquals(firstName, result.get().getFirstName());
        assertEquals(lastName, result.get().getLastName());
        assertEquals(emailAddress, result.get().getEmailAddress());
    }
    @Test
    public void login_FirstNameIsNull_LastNameIsNull_EmailAddressIsNull_ExpectOptionalOfAuthorizedUser() {
        String authGroup = UUID.randomUUID().toString();
        Role role = Role.ADMIN;
        String username = UUID.randomUUID().toString();

        Iso3Response iso3Response = new Iso3ResponseBuilder().withId_token(buildIso3ResponseMap(null, null, null, authGroup)).build();

        stubFor(post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(iso3Response))));

        when(authGroupsService.getAllAuthGroups()).thenReturn(singletonList(authGroup));
        when(authGroupsService.getAuthGroups()).thenReturn(buildAuthGroups(role, authGroup));
        when(authenticatedUserRepository.getByUsername(username)).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.login(username, UUID.randomUUID().toString());

        WireMock.verify(postRequestedFor(urlEqualTo("/token?scope=openid%20panera")));
        verify(authGroupsService, times(1)).getAllAuthGroups();
        verify(authGroupsService, times(1)).getAuthGroups();
        verify(authenticatedUserRepository, times(1)).getByUsername(anyString());
        verify(authenticatedUserRepository, times(0)).delete(any(AuthenticatedUser.class));
        verify(authenticatedUserRepository, times(1)).save(any(AuthenticatedUser.class));

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        assertEquals(role, result.get().getRole());
        assertNull(result.get().getFirstName());
        assertNull(result.get().getLastName());
        assertNull(result.get().getEmailAddress());
    }
    @Test
    public void login_RoleIsCBSS_ExpectOptionalOfAuthorizedUser() {
        String authGroup = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String firstName = UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        Role role = Role.CBSS;
        String username = UUID.randomUUID().toString();

        Iso3Response iso3Response = new Iso3ResponseBuilder().withId_token(buildIso3ResponseMap(emailAddress, firstName, lastName, authGroup)).build();

        stubFor(post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(iso3Response))));

        when(authGroupsService.getAllAuthGroups()).thenReturn(singletonList(authGroup));
        when(authGroupsService.getAuthGroups()).thenReturn(buildAuthGroups(role, authGroup));
        when(authenticatedUserRepository.getByUsername(username)).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.login(username, UUID.randomUUID().toString());

        WireMock.verify(postRequestedFor(urlEqualTo("/token?scope=openid%20panera")));
        verify(authGroupsService, times(1)).getAllAuthGroups();
        verify(authGroupsService, times(1)).getAuthGroups();
        verify(authenticatedUserRepository, times(1)).getByUsername(anyString());
        verify(authenticatedUserRepository, times(0)).delete(any(AuthenticatedUser.class));
        verify(authenticatedUserRepository, times(1)).save(any(AuthenticatedUser.class));

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        assertEquals(role, result.get().getRole());
        assertEquals(firstName, result.get().getFirstName());
        assertEquals(lastName, result.get().getLastName());
        assertEquals(emailAddress, result.get().getEmailAddress());
    }
    @Test
    public void login_RoleIsCBSS_MANAGER_ExpectOptionalOfAuthorizedUser() {
        String authGroup = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String firstName = UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        Role role = Role.CBSS_MANAGER;
        String username = UUID.randomUUID().toString();

        Iso3Response iso3Response = new Iso3ResponseBuilder().withId_token(buildIso3ResponseMap(emailAddress, firstName, lastName, authGroup)).build();

        stubFor(post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(iso3Response))));

        when(authGroupsService.getAllAuthGroups()).thenReturn(singletonList(authGroup));
        when(authGroupsService.getAuthGroups()).thenReturn(buildAuthGroups(role, authGroup));
        when(authenticatedUserRepository.getByUsername(username)).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.login(username, UUID.randomUUID().toString());

        WireMock.verify(postRequestedFor(urlEqualTo("/token?scope=openid%20panera")));
        verify(authGroupsService, times(1)).getAllAuthGroups();
        verify(authGroupsService, times(1)).getAuthGroups();
        verify(authenticatedUserRepository, times(1)).getByUsername(anyString());
        verify(authenticatedUserRepository, times(0)).delete(any(AuthenticatedUser.class));
        verify(authenticatedUserRepository, times(1)).save(any(AuthenticatedUser.class));

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        assertEquals(role, result.get().getRole());
        assertEquals(firstName, result.get().getFirstName());
        assertEquals(lastName, result.get().getLastName());
        assertEquals(emailAddress, result.get().getEmailAddress());
    }
    @Test
    public void login_RoleIsPROD_SUPPORT_ExpectOptionalOfAuthorizedUser() {
        String authGroup = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String firstName = UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        Role role = Role.PROD_SUPPORT;
        String username = UUID.randomUUID().toString();

        Iso3Response iso3Response = new Iso3ResponseBuilder().withId_token(buildIso3ResponseMap(emailAddress, firstName, lastName, authGroup)).build();

        stubFor(post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(iso3Response))));

        when(authGroupsService.getAllAuthGroups()).thenReturn(singletonList(authGroup));
        when(authGroupsService.getAuthGroups()).thenReturn(buildAuthGroups(role, authGroup));
        when(authenticatedUserRepository.getByUsername(username)).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.login(username, UUID.randomUUID().toString());

        WireMock.verify(postRequestedFor(urlEqualTo("/token?scope=openid%20panera")));
        verify(authGroupsService, times(1)).getAllAuthGroups();
        verify(authGroupsService, times(1)).getAuthGroups();
        verify(authenticatedUserRepository, times(1)).getByUsername(anyString());
        verify(authenticatedUserRepository, times(0)).delete(any(AuthenticatedUser.class));
        verify(authenticatedUserRepository, times(1)).save(any(AuthenticatedUser.class));

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        assertEquals(role, result.get().getRole());
        assertEquals(firstName, result.get().getFirstName());
        assertEquals(lastName, result.get().getLastName());
        assertEquals(emailAddress, result.get().getEmailAddress());
    }
    @Test
    public void login_RoleIsSALES_ADMIN_ExpectOptionalOfAuthorizedUser() {
        String authGroup = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String firstName = UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        Role role = Role.SALES_ADMIN;
        String username = UUID.randomUUID().toString();

        Iso3Response iso3Response = new Iso3ResponseBuilder().withId_token(buildIso3ResponseMap(emailAddress, firstName, lastName, authGroup)).build();

        stubFor(post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(iso3Response))));

        when(authGroupsService.getAllAuthGroups()).thenReturn(singletonList(authGroup));
        when(authGroupsService.getAuthGroups()).thenReturn(buildAuthGroups(role, authGroup));
        when(authenticatedUserRepository.getByUsername(username)).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.login(username, UUID.randomUUID().toString());

        WireMock.verify(postRequestedFor(urlEqualTo("/token?scope=openid%20panera")));
        verify(authGroupsService, times(1)).getAllAuthGroups();
        verify(authGroupsService, times(1)).getAuthGroups();
        verify(authenticatedUserRepository, times(1)).getByUsername(anyString());
        verify(authenticatedUserRepository, times(0)).delete(any(AuthenticatedUser.class));
        verify(authenticatedUserRepository, times(1)).save(any(AuthenticatedUser.class));

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        assertEquals(role, result.get().getRole());
        assertEquals(firstName, result.get().getFirstName());
        assertEquals(lastName, result.get().getLastName());
        assertEquals(emailAddress, result.get().getEmailAddress());
    }
    @Test
    public void login_ResponseFromIsoIs4xx_ExpectOptional() {
        String authGroup = UUID.randomUUID().toString();
        Role role = Role.ADMIN;
        String username = UUID.randomUUID().toString();

        stubFor(post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse().withStatus(401)));

        when(authGroupsService.getAllAuthGroups()).thenReturn(singletonList(authGroup));
        when(authGroupsService.getAuthGroups()).thenReturn(buildAuthGroups(role, authGroup));
        when(authenticatedUserRepository.getByUsername(username)).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.login(username, UUID.randomUUID().toString());

        WireMock.verify(postRequestedFor(urlEqualTo("/token?scope=openid%20panera")));
        verify(authGroupsService, times(0)).getAllAuthGroups();
        verify(authGroupsService, times(0)).getAuthGroups();
        verify(authenticatedUserRepository, times(0)).getByUsername(anyString());
        verify(authenticatedUserRepository, times(0)).delete(any(AuthenticatedUser.class));
        verify(authenticatedUserRepository, times(0)).save(any(AuthenticatedUser.class));

        assertFalse(result.isPresent());
    }
    @Test(expected = RuntimeException.class)
    public void login_ResponseFromIsoIs5xx_ExpectOptional() {
        String authGroup = UUID.randomUUID().toString();
        Role role = Role.ADMIN;
        String username = UUID.randomUUID().toString();

        stubFor(post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse().withStatus(500)));

        when(authGroupsService.getAllAuthGroups()).thenReturn(singletonList(authGroup));
        when(authGroupsService.getAuthGroups()).thenReturn(buildAuthGroups(role, authGroup));
        when(authenticatedUserRepository.getByUsername(username)).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.login(username, UUID.randomUUID().toString());

        WireMock.verify(postRequestedFor(urlEqualTo("/token?scope=openid%20panera")));
        verify(authGroupsService, times(0)).getAllAuthGroups();
        verify(authGroupsService, times(0)).getAuthGroups();
        verify(authenticatedUserRepository, times(0)).getByUsername(anyString());
        verify(authenticatedUserRepository, times(0)).delete(any(AuthenticatedUser.class));
        verify(authenticatedUserRepository, times(0)).save(any(AuthenticatedUser.class));

        assertFalse(result.isPresent());
    }
    @Test
    public void login_IdTokenIsNull_ExpectEmptyOptional() {
        String authGroup = UUID.randomUUID().toString();
        Role role = Role.ADMIN;
        String username = UUID.randomUUID().toString();

        Iso3Response iso3Response = new Iso3ResponseBuilder().withId_token((String)null).build();

        stubFor(post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(iso3Response))));

        when(authGroupsService.getAllAuthGroups()).thenReturn(singletonList(authGroup));
        when(authGroupsService.getAuthGroups()).thenReturn(buildAuthGroups(role, authGroup));
        when(authenticatedUserRepository.getByUsername(username)).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.login(username, UUID.randomUUID().toString());

        WireMock.verify(postRequestedFor(urlEqualTo("/token?scope=openid%20panera")));
        verify(authGroupsService, times(0)).getAllAuthGroups();
        verify(authGroupsService, times(0)).getAuthGroups();
        verify(authenticatedUserRepository, times(0)).getByUsername(anyString());
        verify(authenticatedUserRepository, times(0)).delete(any(AuthenticatedUser.class));
        verify(authenticatedUserRepository, times(0)).save(any(AuthenticatedUser.class));

        assertFalse(result.isPresent());
    }
    @Test
    public void login_RoleIsNull_ExpectEmptyOptional() {
        String authGroup = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String firstName = UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        Role role = Role.ADMIN;
        String username = UUID.randomUUID().toString();

        Iso3Response iso3Response = new Iso3ResponseBuilder().withId_token(buildIso3ResponseMap(emailAddress, firstName, lastName, (String)null)).build();

        stubFor(post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(iso3Response))));

        when(authGroupsService.getAllAuthGroups()).thenReturn(singletonList(authGroup));
        when(authGroupsService.getAuthGroups()).thenReturn(buildAuthGroups(role, authGroup));
        when(authenticatedUserRepository.getByUsername(username)).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.login(username, UUID.randomUUID().toString());

        WireMock.verify(postRequestedFor(urlEqualTo("/token?scope=openid%20panera")));
        verify(authGroupsService, times(0)).getAllAuthGroups();
        verify(authGroupsService, times(0)).getAuthGroups();
        verify(authenticatedUserRepository, times(0)).getByUsername(anyString());
        verify(authenticatedUserRepository, times(0)).delete(any(AuthenticatedUser.class));
        verify(authenticatedUserRepository, times(0)).save(any(AuthenticatedUser.class));

        assertFalse(result.isPresent());
    }
    @Test
    public void login_DoesNotBelongToAValidGroup_ExpectEmptyOptional() {
        String authGroup = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String firstName = UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        Role role = Role.ADMIN;
        String username = UUID.randomUUID().toString();

        Iso3Response iso3Response = new Iso3ResponseBuilder().withId_token(buildIso3ResponseMap(emailAddress, firstName, lastName, UUID.randomUUID().toString())).build();

        stubFor(post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(iso3Response))));

        when(authGroupsService.getAllAuthGroups()).thenReturn(singletonList(authGroup));
        when(authGroupsService.getAuthGroups()).thenReturn(buildAuthGroups(role, authGroup));
        when(authenticatedUserRepository.getByUsername(username)).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.login(username, UUID.randomUUID().toString());

        WireMock.verify(postRequestedFor(urlEqualTo("/token?scope=openid%20panera")));
        verify(authGroupsService, times(1)).getAllAuthGroups();
        verify(authGroupsService, times(0)).getAuthGroups();
        verify(authenticatedUserRepository, times(0)).getByUsername(anyString());
        verify(authenticatedUserRepository, times(0)).delete(any(AuthenticatedUser.class));
        verify(authenticatedUserRepository, times(0)).save(any(AuthenticatedUser.class));

        assertFalse(result.isPresent());
    }
    @Test
    public void login_DoesNotHaveAValidRole_ExpectEmptyOptional() {
        String authGroup = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String firstName = UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        Role role = Role.ADMIN;
        String username = UUID.randomUUID().toString();

        Iso3Response iso3Response = new Iso3ResponseBuilder().withId_token(buildIso3ResponseMap(emailAddress, firstName, lastName, authGroup)).build();

        stubFor(post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(iso3Response))));

        when(authGroupsService.getAllAuthGroups()).thenReturn(singletonList(authGroup));
        when(authGroupsService.getAuthGroups()).thenReturn(buildAuthGroups(role, UUID.randomUUID().toString()));
        when(authenticatedUserRepository.getByUsername(username)).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.login(username, UUID.randomUUID().toString());

        WireMock.verify(postRequestedFor(urlEqualTo("/token?scope=openid%20panera")));
        verify(authGroupsService, times(1)).getAllAuthGroups();
        verify(authGroupsService, times(1)).getAuthGroups();
        verify(authenticatedUserRepository, times(0)).getByUsername(anyString());
        verify(authenticatedUserRepository, times(0)).delete(any(AuthenticatedUser.class));
        verify(authenticatedUserRepository, times(0)).save(any(AuthenticatedUser.class));

        assertFalse(result.isPresent());
    }
    @Test
    public void login_NoRolesSet_ExpectEmptyOptional() {
        String authGroup = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String firstName = UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        Role role = Role.ADMIN;
        String username = UUID.randomUUID().toString();

        Iso3Response iso3Response = new Iso3ResponseBuilder().withId_token(buildIso3ResponseMap(emailAddress, firstName, lastName, authGroup)).build();

        stubFor(post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(iso3Response))));

        when(authGroupsService.getAllAuthGroups()).thenReturn(singletonList(authGroup));
        when(authGroupsService.getAuthGroups()).thenReturn(buildAuthGroups(role, null));
        when(authenticatedUserRepository.getByUsername(username)).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.login(username, UUID.randomUUID().toString());

        WireMock.verify(postRequestedFor(urlEqualTo("/token?scope=openid%20panera")));
        verify(authGroupsService, times(1)).getAllAuthGroups();
        verify(authGroupsService, times(1)).getAuthGroups();
        verify(authenticatedUserRepository, times(0)).getByUsername(anyString());
        verify(authenticatedUserRepository, times(0)).delete(any(AuthenticatedUser.class));
        verify(authenticatedUserRepository, times(0)).save(any(AuthenticatedUser.class));

        assertFalse(result.isPresent());
    }
    @Test
    public void login_RolesIsEmptyList_ExpectEmptyOptional() {
        String authGroup = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String firstName = UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        Role role = Role.ADMIN;
        String username = UUID.randomUUID().toString();

        Iso3Response iso3Response = new Iso3ResponseBuilder().withId_token(buildIso3ResponseMap(emailAddress, firstName, lastName, authGroup)).build();

        stubFor(post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(iso3Response))));

        Map<String, List<String>> authGroups = new HashMap<String, List<String>>() {{
            put(APP_CONFIG_AUTH_GROUP + "." + role.name().toLowerCase(), emptyList());
        }};

        when(authGroupsService.getAllAuthGroups()).thenReturn(singletonList(authGroup));
        when(authGroupsService.getAuthGroups()).thenReturn(authGroups);
        when(authenticatedUserRepository.getByUsername(username)).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.login(username, UUID.randomUUID().toString());

        WireMock.verify(postRequestedFor(urlEqualTo("/token?scope=openid%20panera")));
        verify(authGroupsService, times(1)).getAllAuthGroups();
        verify(authGroupsService, times(1)).getAuthGroups();
        verify(authenticatedUserRepository, times(0)).getByUsername(anyString());
        verify(authenticatedUserRepository, times(0)).delete(any(AuthenticatedUser.class));
        verify(authenticatedUserRepository, times(0)).save(any(AuthenticatedUser.class));

        assertFalse(result.isPresent());
    }
    @Test
    public void login_UsernameIsNull_ExpectEmptyOptional() {
        String authGroup = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String firstName = UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        Role role = Role.ADMIN;
        String username = UUID.randomUUID().toString();

        Iso3Response iso3Response = new Iso3ResponseBuilder().withId_token(buildIso3ResponseMap(emailAddress, firstName, lastName, authGroup)).build();

        stubFor(post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(iso3Response))));

        when(authGroupsService.getAllAuthGroups()).thenReturn(singletonList(authGroup));
        when(authGroupsService.getAuthGroups()).thenReturn(buildAuthGroups(role, authGroup));
        when(authenticatedUserRepository.getByUsername(username)).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.login(null, UUID.randomUUID().toString());

        WireMock.verify(0, postRequestedFor(urlEqualTo("/token?scope=openid%20panera")));
        verify(authGroupsService, times(0)).getAllAuthGroups();
        verify(authGroupsService, times(0)).getAuthGroups();
        verify(authenticatedUserRepository, times(0)).getByUsername(anyString());
        verify(authenticatedUserRepository, times(0)).delete(any(AuthenticatedUser.class));
        verify(authenticatedUserRepository, times(0)).save(any(AuthenticatedUser.class));

        assertFalse(result.isPresent());
    }
    @Test
    public void login_PasswordIsNull_ExpectEmptyOptional() {
        String authGroup = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String firstName = UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        Role role = Role.ADMIN;
        String username = UUID.randomUUID().toString();

        Iso3Response iso3Response = new Iso3ResponseBuilder().withId_token(buildIso3ResponseMap(emailAddress, firstName, lastName, authGroup)).build();

        stubFor(post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(iso3Response))));

        when(authGroupsService.getAllAuthGroups()).thenReturn(singletonList(authGroup));
        when(authGroupsService.getAuthGroups()).thenReturn(buildAuthGroups(role, authGroup));
        when(authenticatedUserRepository.getByUsername(username)).thenReturn(Optional.empty());

        Optional<AuthenticatedUser> result = classUnderTest.login(username, null);

        WireMock.verify(0, postRequestedFor(urlEqualTo("/token?scope=openid%20panera")));
        verify(authGroupsService, times(0)).getAllAuthGroups();
        verify(authGroupsService, times(0)).getAuthGroups();
        verify(authenticatedUserRepository, times(0)).getByUsername(anyString());
        verify(authenticatedUserRepository, times(0)).delete(any(AuthenticatedUser.class));
        verify(authenticatedUserRepository, times(0)).save(any(AuthenticatedUser.class));

        assertFalse(result.isPresent());
    }

    @Test
    public void logout_AuthenticatedUserIsSet() {
        classUnderTest.logout();

        verify(authenticatedUserRepository, times(1)).delete(any(AuthenticatedUser.class));
    }
    @Test
    public void logout_AuthenticatedUserIsNotSet() {
        AuthenticatedUserManager.setAuthenticatedUser(null);

        classUnderTest.logout();

        verify(authenticatedUserRepository, times(0)).delete(any(AuthenticatedUser.class));
    }

    @Test
    public void updateSession_AuthenticatedUserExists_AuthenticatedUserIsUpdated() {
        String sessionId = UUID.randomUUID().toString();
        AuthenticatedUser authUser = new AuthenticatedUserBuilder().build();

        when(authenticatedUserRepository.getById(sessionId)).thenReturn(Optional.of(authUser));

        classUnderTest.updateSession(sessionId, authUser);

        verify(authenticatedUserRepository, times(1)).getById(anyString());
        verify(authenticatedUserRepository, times(1)).save(any(AuthenticatedUser.class));
    }
    @Test
    public void updateSession_AuthenticatedUserDoesNotExist() {
        String sessionId = UUID.randomUUID().toString();
        AuthenticatedUser authUser = new AuthenticatedUserBuilder().build();

        when(authenticatedUserRepository.getById(sessionId)).thenReturn(Optional.empty());

        classUnderTest.updateSession(sessionId, authUser);

        verify(authenticatedUserRepository, times(1)).getById(anyString());
        verify(authenticatedUserRepository, times(0)).save(any(AuthenticatedUser.class));
    }
    @Test
    public void updateSession_SessionIdIsNull() {
        String sessionId = UUID.randomUUID().toString();
        AuthenticatedUser authUser = new AuthenticatedUserBuilder().build();

        when(authenticatedUserRepository.getById(sessionId)).thenReturn(Optional.of(authUser));

        classUnderTest.updateSession(null, authUser);

        verify(authenticatedUserRepository, times(0)).getById(anyString());
        verify(authenticatedUserRepository, times(0)).save(any(AuthenticatedUser.class));
    }
    @Test
    public void updateSession_UpdatedAuthenticatedUserIsNull() {
        String sessionId = UUID.randomUUID().toString();
        AuthenticatedUser authUser = new AuthenticatedUserBuilder().build();

        when(authenticatedUserRepository.getById(sessionId)).thenReturn(Optional.of(authUser));

        classUnderTest.updateSession(sessionId, null);

        verify(authenticatedUserRepository, times(0)).getById(anyString());
        verify(authenticatedUserRepository, times(0)).save(any(AuthenticatedUser.class));
    }

    private Map<String, List<String>> buildAuthGroups(Role role, String adminGroupName) {
        return new HashMap<String, List<String>>() {{
            put(APP_CONFIG_AUTH_GROUP + "." + role.name().toLowerCase(), (adminGroupName != null) ? singletonList(adminGroupName) : null);
        }};
    }
}