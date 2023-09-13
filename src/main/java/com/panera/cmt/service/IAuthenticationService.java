package com.panera.cmt.service;

import com.panera.cmt.mongo.entity.AuthenticatedUser;

import java.util.Optional;

public interface IAuthenticationService {

    Optional<AuthenticatedUser> getAuthenticatedUser();

    Optional<AuthenticatedUser> getAuthenticatedUser(String accessToken);

    Optional<AuthenticatedUser> login(String username, String password);

    void logout();

    void updateSession(String sessionId, AuthenticatedUser authenticatedUser);
}
