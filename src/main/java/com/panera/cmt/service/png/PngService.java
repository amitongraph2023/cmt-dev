package com.panera.cmt.service.png;
import com.google.gson.Gson;
import com.panera.cmt.config.Constants;
import com.panera.cmt.dto.proxy.subscription_service.GiftCoffeeEmail;
import com.panera.cmt.entity.AppConfig;
import com.panera.cmt.enums.ServerApplication;
import com.panera.cmt.exceptions.ResendGiftCoffeeSubscriptionEmailClientException;
import com.panera.cmt.service.app_config.IAppConfigLocalService;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_LMT;
import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_PNG;
import static java.util.Collections.singletonMap;

@Service
@Slf4j
@SuppressWarnings("unused")
public class PngService extends BasePanaraNotificationGatewayService implements IPngService {

    @Value("${png.auth}")
    private String client_config_png_auth;

    @Value("${png.base-url}")
    private String pngBaseUrl;

    private final IAppConfigLocalService appConfigService;

    public PngService(IAppConfigLocalService appConfigService) {
    this.appConfigService = appConfigService;
    }

    @Override
    @Retryable(value = {ResendGiftCoffeeSubscriptionEmailClientException.class}, backoff = @Backoff(delay = 500))
    public Boolean resendGiftCoffeeSubscriptionEmail(GiftCoffeeEmail giftCoffeeEmail, String giftCode) {

        AppConfig templateId = appConfigService.getAppConfigByCode(Constants.APP_CONFIG_GIFT_COFFEE_TEMPLATE_ID).orElse(null);
        if (templateId.getValue() == null || giftCode == null) {
            return false;
        }

        Map<String, Object> requestBody = new LinkedHashMap<String, Object>(){{
            put("customerId", giftCoffeeEmail.getCustomerId());
            put("templateId", templateId);
            put("attributes", new LinkedHashMap<String, Object>(){{
                put("giftCode", giftCode);
                put("gifterEmail", giftCoffeeEmail.getPurchaserEmail());
                put("gifterName", giftCoffeeEmail.getPurchaserEmail());
                put("giftName", giftCoffeeEmail.getDescription());
                put("orderId", giftCoffeeEmail.getPurchaseOrderId());
            }});
        }};

        StopWatch stopWatch = new StopWatch(log, "resendGiftCoffeeSubscriptionEmail", String.format("Sending email templateId=%s for giftCode=%s", templateId, giftCode));

        String response = doCallToServer(String.class, stopWatch, HttpMethod.POST, ServerApplication.PNG, new JSONObject(requestBody).toString(), "/notification");

        if (response == null) {
            stopWatch.checkPoint("Error with request");
            throw new ResendGiftCoffeeSubscriptionEmailClientException();
        }

        return true;
    }

    private <T> T doCallToServer(Class<T> clazz, StopWatch stopWatch, HttpMethod method, ServerApplication application, Object body, String uri, Object... params) {
        WebClient client = getWebClient(application, stopWatch, body);

        ClientResponse response = null;
        switch (method) {
            case GET:
                response = client.get()
                        .uri(uri, params)
                        .accept(MediaType.APPLICATION_JSON)
                        .acceptCharset(StandardCharsets.UTF_8)
                        .exchange()
                        .timeout(Duration.ofMillis(2000))
                        .block();
                break;
            case POST:
                response = client.post()
                        .uri(uri, params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .syncBody(body)
                        .exchange()
                        .timeout(Duration.ofMillis(2000))
                        .block();
                break;
            case PUT:
                response = client.put()
                        .uri(uri, params)
                        .exchange()
                        .timeout(Duration.ofMillis(2000))
                        .block();
                break;
        }

        if (response != null) {
            T converted = response.bodyToMono(clazz).block();

            logResponse(stopWatch, response, converted);

            return converted;
        } else {
            stopWatch.checkPoint("Error connecting");
            return null;
        }
    }

    private Mono<ClientResponse> filterLogger(ClientRequest r, ExchangeFunction n, StopWatch s, Object b) {
        s.checkPoint(String.format("Request to server method=%s, url=%s, body=%s", r.method().toString().toUpperCase(), r.url().toString(), new Gson().toJson(b)));
        return n.exchange(r);
    }

    private WebClient getWebClient(ServerApplication application, StopWatch stopWatch, Object body) {
        switch (application) {
            case PNG:
                return WebClient
                        .builder()
                        .baseUrl(pngBaseUrl)
                        .filter((request, next) -> filterLogger(request, next, stopWatch, body))
                        .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + client_config_png_auth)
                        .defaultUriVariables(singletonMap("url", pngBaseUrl))
                        .build();
            default:
                throw new NotImplementedException("The given ServerApplication is not implemented");
        }
    }



    private void logResponse(StopWatch stopWatch, ClientResponse response, Object entity) {
        stopWatch.checkPoint(String.format("Response from server status=%d, body=%s", response.statusCode().value(), new Gson().toJson(entity)));
    }

    @Override
    protected String getSubjectName() {
        return AUDIT_SUBJECT_PNG;
    }
}
