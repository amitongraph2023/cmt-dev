package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.*;

import java.util.List;
import java.util.Random;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class CustomerDetailsBuilder extends BaseObjectBuilder<CustomerDetails> {

    private Long customerId = new Random().nextLong();
    private String username = randomAlphabetic(10);
    private String firstName = randomAlphabetic(10);
    private String lastName = randomAlphabetic(10);
    private boolean isEmailGlobalOpt = new Random().nextBoolean();
    private boolean isSmsGlobalOpt = new Random().nextBoolean();
    private boolean isMobilePushOpt = new Random().nextBoolean();
    private Loyalty loyalty = new LoyaltyBuilder().build();
    private BirthDate birthDate = new BirthDateBuilder().build();
    private SocialIntegrations socialIntegration = new SocialIntegrationsBuilder().build();
    private List<TaxExemption> taxExemptions = asList(new TaxExemptionBuilder().build());
    private boolean isSuspended = false;

    @Override
    CustomerDetails getTestClass() {
        return new CustomerDetails();
    }
}
