package com.panera.cmt.config.apigee.paytronix;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class PaytronixApigeeErrorHandler implements ResponseErrorHandler {

    private final Logger logger = LoggerFactory.getLogger(PaytronixApigeeErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
    }

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        logger.info("Status code: {}, Status text: {}", clientHttpResponse.getStatusCode(),
                clientHttpResponse.getStatusText());
        return !clientHttpResponse.getStatusCode().is2xxSuccessful();
    }

}
