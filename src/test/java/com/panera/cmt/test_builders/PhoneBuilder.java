package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.Phone;

import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public class PhoneBuilder extends BaseObjectBuilder<Phone> {

    private Long id = new Random().nextLong();
    private String phoneNumber = randomNumeric(10);
    private String phoneType = "Residential";
    private String countryCode = "1";
    private String extension = randomAlphabetic(10);
    private String name = randomAlphabetic(10);
    private boolean isCallOpt = false;
    private boolean isDefault = false;
    private boolean isValid = false;

    @Override
    Phone getTestClass() {
        return new Phone();
    }
}
