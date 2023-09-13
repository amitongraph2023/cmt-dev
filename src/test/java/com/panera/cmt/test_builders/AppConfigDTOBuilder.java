package com.panera.cmt.test_builders;

import com.panera.cmt.dto.app_config.AppConfigDTO;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static com.panera.cmt.test_util.SharedTestUtil.DateType.PAST;
import static com.panera.cmt.test_util.SharedTestUtil.randomDate;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class AppConfigDTOBuilder extends BaseObjectBuilder<AppConfigDTO> {

    private Long id = new Random().nextLong();
    private String code = randomAlphanumeric(50);
    private String value = UUID.randomUUID().toString();
    private Date lastUpdatedAt = randomDate(PAST);
    private String lastUpdatedBy = UUID.randomUUID().toString();

    public AppConfigDTOBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public AppConfigDTOBuilder withCode(String code) {
        this.code = code;
        return this;
    }

    public AppConfigDTOBuilder withValue(String value) {
        this.value = value;
        return this;
    }

    public AppConfigDTOBuilder withLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
        return this;
    }

    public AppConfigDTOBuilder withLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
        return this;
    }

    @Override
    AppConfigDTO getTestClass() {
        return new AppConfigDTO();
    }
}
