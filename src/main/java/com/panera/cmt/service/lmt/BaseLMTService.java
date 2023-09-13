package com.panera.cmt.service.lmt;

import com.google.gson.Gson;
import com.panera.cmt.config.Constants;
import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.ProxyUserDTO;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.LMTEndpoints;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.Audit;
import com.panera.cmt.mongo.repository.IAuditRepository;
import com.panera.cmt.service.app_config.IAppConfigLocalService;
import com.panera.cmt.util.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.panera.cmt.config.Constants.AUDIT_EXPIRATION_DEFAULT_UNIT;
import static com.panera.cmt.util.MongoUtils.addToCurrentDate;
import static java.util.Collections.singletonMap;

public abstract class BaseLMTService {
    @Value("${lmt.auth}")
    protected String auth;

    @Value("${lmt.base-url}")
    protected String baseUrl;

    protected IAppConfigLocalService appConfigService;

    private IAuditRepository auditRepository;

    @Autowired
    public void setAppConfigService(IAppConfigLocalService appConfigService) { this.appConfigService = appConfigService; }

    @Autowired
    public void setAuditRepository(IAuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    protected abstract String getSubjectName();

    protected Audit createAudit(ActionType actionType, Long personId, Object payload) {
        Audit audit = new Audit();
        audit.setActionType(actionType);
        audit.setSubject(getSubjectName());
        audit.setPersonId(personId);
        audit.setPayload(payload);
        audit.setExpirationDate(generateExpirationDate());
        return audit;
    }
    protected Audit createAudit(ActionType actionType, Long personId, Long objectId, Object payload) {
        Audit audit = createAudit(actionType, personId, payload);
        audit.setObjectId(objectId);
        return audit;
    }

    protected <T> ResponseHolder<T> doDelete(Class<T> clazz, StopWatch stopWatch, Audit audit, LMTEndpoints stub, Object... params) {
        return doSendResponse(clazz, stopWatch, audit, HttpMethod.DELETE, null, stub, params);
    }

    protected <T> T doGet(Class<T> clazz, StopWatch stopWatch, LMTEndpoints stub, Object... params) {
        return doSend(clazz, stopWatch, HttpMethod.GET, null, stub, params);
    }

    protected <T> ResponseHolder<T> doGetResponse(Class<T> clazz, StopWatch stopWatch, LMTEndpoints stub, Object... params) {
        return doSendResponse(clazz, stopWatch, null, HttpMethod.GET, null, stub, params);
    }

    protected <T> ResponseHolder<T> doPost(Class<T> clazz, StopWatch stopWatch, Audit audit, Object body, LMTEndpoints stub, Object... params) {
        return doSendResponse(clazz, stopWatch, audit, HttpMethod.POST, body, stub, params);
    }

    protected <T> ResponseHolder<T> doPut(Class<T> clazz, StopWatch stopWatch, Audit audit, Object body, LMTEndpoints stub, Object... params) {
        return doSendResponse(clazz, stopWatch, audit, HttpMethod.PUT, body, stub, params);
    }

    private <T> ResponseHolder<T> doSendResponse(Class<T> clazz, StopWatch stopWatch, Audit audit, HttpMethod method, Object body, LMTEndpoints stub, Object... params) {
        ProxyUserDTO proxyUserDTO = ProxyUserDTO.fromAuthenticatedUser(AuthenticatedUserManager.getAuthenticatedUser());
        WebClient client = WebClient
                .builder()
                .baseUrl(baseUrl)
                .filter((request, next) -> {
                    stopWatch.checkPoint(String.format("Request to server method=%s, url=%s, body=%s", request.method().toString().toUpperCase(), request.url().toString(), new Gson().toJson(body).replaceAll("([Pp])assword\"[ ]?:\".*?\"", "$1assword\":\"*****\"")));

                    return next.exchange(request);
                })
                .defaultHeaders(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, "Basic " + auth);
                    headers.add("proxyUser", proxyUserDTO.getUsername());
                })
                .defaultUriVariables(singletonMap("url", baseUrl))
                .build();

        ClientResponse response = null;
        switch (method) {
            case DELETE:
                response = client.delete()
                        .uri(stub.getStub(), params)
                        .exchange()
                        .block();
                break;
            case GET:
                response = client.get()
                        .uri(stub.getStub(), params)
                        .exchange()
                        .block();
                break;
            case POST:
                if (body != null) {
                    response = client.post()
                            .uri(stub.getStub(), params)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.fromObject(body))
                            .exchange()
                            .block();
                } else {
                    response = client.post()
                            .uri(stub.getStub(), params)
                            .exchange()
                            .block();
                }
                break;
            case PUT:
                response = client.put()
                        .uri(stub.getStub(), params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromObject(body))
                        .exchange()
                        .block();
                break;
        }

        if (response != null) {
            ResponseHolder<T> responseHolder = new ResponseHolder<>();
            responseHolder.setStatus(response.statusCode());

            if (audit != null) {
                audit.setStatusCode(response.statusCode().value());
                auditRepository.save(audit);
            }

            if (response.statusCode().equals(HttpStatus.NOT_FOUND) || response.statusCode().is5xxServerError()) {
                logResponse(stopWatch, response, new Object());
            } else if (response.statusCode().is4xxClientError()) {
                responseHolder.setErrors(response.bodyToMono(AllErrorsDTO.class).block());
                logResponse(stopWatch, response, responseHolder.getErrors());
            } else {
                responseHolder.setEntity(response.bodyToMono(clazz).block());
                logResponse(stopWatch, response, responseHolder.getEntity());
            }

            return (!responseHolder.getStatus().is5xxServerError()) ? responseHolder : null;
        } else {
            stopWatch.checkPoint("Error connecting with customer hub");
            return null;
        }
    }
    private <T> T doSend(Class<T> clazz, StopWatch stopWatch, HttpMethod method, Object body, LMTEndpoints stub, Object... params) {
        ProxyUserDTO proxyUserDTO = ProxyUserDTO.fromAuthenticatedUser(AuthenticatedUserManager.getAuthenticatedUser());
        WebClient client = WebClient
                .builder()
                .baseUrl(baseUrl)
                .filter((request, next) -> {
                    stopWatch.checkPoint(String.format("Request to server method=%s, url=%s, body=%s", request.method().toString().toUpperCase(), request.url().toString(), new Gson().toJson(body)));

                    return next.exchange(request);
                })
                .defaultHeaders(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, "Basic " + auth);
                    headers.add("proxyUser", proxyUserDTO.getUsername());
                })
                .defaultUriVariables(singletonMap("url", baseUrl))
                .build();

        ClientResponse response = null;
        switch (method) {
            case DELETE:
                response = client.delete()
                        .uri(stub.getStub(), params)
                        .exchange()
                        .block();
                break;
            case GET:
                response = client.get()
                        .uri(stub.getStub(), params)
                        .exchange()
                        .block();
                break;
        }

        if (response != null) {
            T converted = response.bodyToMono(clazz).block();

            logResponse(stopWatch, response, converted);

            return converted;
        } else {
            stopWatch.checkPoint("Error connecting with customer hub");
            return null;
        }
    }

    private void logResponse(StopWatch stopWatch, ClientResponse response, Object entity) {
        stopWatch.checkPoint(String.format("Response from server status=%d, body=%s", response.statusCode().value(), new Gson().toJson(entity)));
    }

    private Date generateExpirationDate() {
        TimeUnit timeUnit = appConfigService.getAppConfigTimeEnumValueByCode(Constants.APP_CONFIG_AUDIT_RETENTION_TIME_UNIT).orElse(TimeUnit.DAYS);
        Integer amount = appConfigService.getAppConfigIntValueByCode(Constants.APP_CONFIG_AUDIT_RETENTION_TIME_AMOUNT).orElse(AUDIT_EXPIRATION_DEFAULT_UNIT);
        return addToCurrentDate(timeUnit, amount);
    }
}
