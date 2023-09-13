package com.panera.cmt.test_builders;

import com.panera.cmt.dto.AuthRequestDTO;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class AuthRequestDTOBuilder extends BaseObjectBuilder<AuthRequestDTO> {

    private String username = randomAlphabetic(20);
    private String password = randomAlphabetic(20);

    public AuthRequestDTOBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public AuthRequestDTOBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    AuthRequestDTO getTestClass() {
        return new AuthRequestDTO();
    }
}
