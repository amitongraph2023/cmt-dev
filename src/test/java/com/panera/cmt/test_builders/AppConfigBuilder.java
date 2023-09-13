package com.panera.cmt.test_builders;

import com.panera.cmt.entity.AppConfig;

import java.util.Random;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class AppConfigBuilder extends BaseObjectBuilder<AppConfig> {

    private Long id = new Random().nextLong();
    private String code = randomAlphanumeric(50);
    private String value = UUID.randomUUID().toString();

    public AppConfigBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public AppConfigBuilder withCode(String code) {
        this.code = code;
        return this;
    }

    public AppConfigBuilder withValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    AppConfig getTestClass() {
        return new AppConfig();
    }
}
