package com.panera.cmt.config.client_auth;

import lombok.Data;

@Data
public class ConfigUser {

    private String username;
    private String password;
    private String roles;
    private boolean requireAuth;
}
