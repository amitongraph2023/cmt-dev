package com.panera.cmt.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ApiModel(value="Authentication ", description="Information for the user that is logging in")
@Data
public class AuthRequestDTO {

    @NotNull(message = "error.username.required")
    private String username;

    @NotNull(message = "error.password.required")
    private String password;

    @Override
    public String toString() {
        return "{" +
                "\"username\":\"" + username + "\"" +
                ",\"password\":\"******\"" +
                '}';
    }
}
