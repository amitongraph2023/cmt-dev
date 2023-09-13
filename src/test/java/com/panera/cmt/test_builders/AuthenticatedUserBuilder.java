package com.panera.cmt.test_builders;

import com.panera.cmt.enums.Role;
import com.panera.cmt.mongo.entity.AuthenticatedUser;

import java.util.Date;
import java.util.UUID;

import static com.panera.cmt.test_util.SharedTestUtil.DateType.FUTURE;
import static com.panera.cmt.test_util.SharedTestUtil.nextEnum;
import static com.panera.cmt.test_util.SharedTestUtil.randomDateTime;

public class AuthenticatedUserBuilder extends BaseObjectBuilder<AuthenticatedUser> {

    private String id = UUID.randomUUID().toString();
    private String username = UUID.randomUUID().toString();
    private String accessToken = UUID.randomUUID().toString();
    private Role role = nextEnum(Role.class);
    private String firstName = UUID.randomUUID().toString();
    private String lastName = UUID.randomUUID().toString();
    private String emailAddress = UUID.randomUUID().toString();
    private Date loginDate = new Date();
    private Date expirationDate = randomDateTime(FUTURE);

    public AuthenticatedUserBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public AuthenticatedUserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public AuthenticatedUserBuilder withAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public AuthenticatedUserBuilder withRole(Role role) {
        this.role = role;
        return this;
    }

    public AuthenticatedUserBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public AuthenticatedUserBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public AuthenticatedUserBuilder withEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    public AuthenticatedUserBuilder withLoginDate(Date loginDate) {
        this.loginDate = loginDate;
        return this;
    }

    public AuthenticatedUserBuilder withExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    @Override
    AuthenticatedUser getTestClass() {
        return new AuthenticatedUser();
    }
}
