package com.panera.cmt.mongo.repository;

import com.panera.cmt.mongo.entity.AuthenticatedUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface IAuthenticatedUserRepository extends MongoRepository<AuthenticatedUser, String> {

    Optional<AuthenticatedUser> getByAccessToken(String accessToken);

    Optional<AuthenticatedUser> getById(String id);

    Optional<AuthenticatedUser> getByUsername(String username);
}
