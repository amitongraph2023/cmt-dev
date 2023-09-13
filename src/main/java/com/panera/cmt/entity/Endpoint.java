package com.panera.cmt.entity;

import com.panera.cmt.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Endpoint {

    private String endpoint;
    private List<String> methods;
    private List<Role> roles;

    public String getEndpoint() {
        return endpoint.toLowerCase();
    }

    public Endpoint(String endpoint, List<String> methods) {
        this.endpoint = endpoint;
        this.methods = methods;
    }
}
