package com.panera.cmt.test_builders;

import com.panera.cmt.dto.chub.PhoneDTO;

import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public class PhoneDTOBuilder extends BaseObjectBuilder<PhoneDTO> {

    private Long id = new Random().nextLong();
    private String phoneNumber = randomNumeric(10);
    private String phoneType = "Residential";
    private String countryCode = "1";
    private String extension = randomAlphanumeric(10);
    private String name = randomAlphanumeric(10);
    private boolean isCallOpt = false;
    private boolean isDefault = false;
    private boolean isValid = false;

    @Override
    PhoneDTO getTestClass() {
        return new PhoneDTO();
    }
}
