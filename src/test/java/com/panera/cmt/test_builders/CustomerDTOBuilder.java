package com.panera.cmt.test_builders;

import com.panera.cmt.dto.chub.CustomerDTO;

import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class CustomerDTOBuilder extends BaseObjectBuilder<CustomerDTO> {

    private Long customerId = new Random().nextLong();
    private String username = randomAlphabetic(10);
    private String firstName = randomAlphabetic(10);
    private String lastName = randomAlphabetic(10);
    private boolean isEmailGlobalOpt = new Random().nextBoolean();
    private boolean isSmsGlobalOpt = new Random().nextBoolean();
    private boolean isMobilePushOpt = new Random().nextBoolean();

    @Override
    CustomerDTO getTestClass() {
        return new CustomerDTO();
    }
}
