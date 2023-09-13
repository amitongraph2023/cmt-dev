package com.panera.cmt.config.apigee.paytronix;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Profile("!test")
@Import(ApigeePaytronixPropertiesConfig.class)
@Configuration
public class PaytronixApigeeConfig {

    @Autowired
    private ApigeePaytronixPropertiesConfig apigeePaytronixPropertiesConfig;

    @Bean(name="paytronixApigeeRestTemplate")
    public RestTemplate getPaytronixRestTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(requestFactory);
        restTemplate.setErrorHandler(new PaytronixApigeeErrorHandler());
        ((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(apigeePaytronixPropertiesConfig.getReadTimeout());
        ((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(apigeePaytronixPropertiesConfig.getConnectionTimeout());
        return restTemplate;
    }
}
