package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.SearchCustomer;

import java.util.Random;
import java.util.UUID;

import static com.panera.cmt.test_util.SharedTestUtil.randomEmailAddress;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public class SearchCustomerBuilder extends BaseObjectBuilder<SearchCustomer> {

    private Long customerId = new Random().nextLong();
    private String username = UUID.randomUUID().toString();
    private String firstName = UUID.randomUUID().toString();
    private String lastName = UUID.randomUUID().toString();
    private String defaultEmail = randomEmailAddress();
    private String defaultPhone = randomNumeric(10);

    public SearchCustomerBuilder withCustomerId(Long customerId) {
        this.customerId = customerId;
        return this;
    }

    public SearchCustomerBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public SearchCustomerBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public SearchCustomerBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public SearchCustomerBuilder withDefaultEmail(String defaultEmail) {
        this.defaultEmail = defaultEmail;
        return this;
    }

    public SearchCustomerBuilder withDefaultPhone(String defaultPhone) {
        this.defaultPhone = defaultPhone;
        return this;
    }

    @Override
    SearchCustomer getTestClass() {
        return new SearchCustomer();
    }
}
