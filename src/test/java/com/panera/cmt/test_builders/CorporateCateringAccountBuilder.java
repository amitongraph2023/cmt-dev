package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.CorporateCateringAccount;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public class CorporateCateringAccountBuilder extends BaseObjectBuilder<CorporateCateringAccount> {

    private Long orgNumber = new Random().nextLong();
    private String ccaNumber = UUID.randomUUID().toString();
    private Date clientStartDate = new Date();
    private Date clientEndDate = new Date();
    private Date orgStartDate = new Date();
    private Date orgEndDate = new Date();

    private String ccaBillingName = UUID.randomUUID().toString();
    private String addressLine1 = UUID.randomUUID().toString();
    private String addressLine2 = UUID.randomUUID().toString();
    private String city = UUID.randomUUID().toString();
    private String state = randomAlphabetic(2).toUpperCase();
    private String zipCode = randomNumeric(10);
    private String country = randomAlphabetic(2).toUpperCase();
    private Boolean poRequired = false;
    private Boolean onlineEnabled = true;

    @Override
    CorporateCateringAccount getTestClass() {
        return new CorporateCateringAccount();
    }
}
