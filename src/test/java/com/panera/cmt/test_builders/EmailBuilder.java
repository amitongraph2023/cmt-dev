package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.Email;

import java.util.Random;

import static com.panera.cmt.test_util.SharedTestUtil.randomEmailAddress;

public class EmailBuilder extends BaseObjectBuilder<Email> {

    private Long id = new Random().nextLong();
    private String emailAddress = randomEmailAddress();
    private String emailType = "Personal";
    private boolean isDefault = true;
    private boolean isOpt = true;
    private boolean isVerified = true;

    @Override
    Email getTestClass() {
        return new Email();
    }
}
