package com.panera.cmt.entity;

import lombok.Data;

@Data
public class Iso3Response {
    
    private String access_token;
    private String refresh_token;
    private String scope;
    private String id_token;
    private String token_type;
    private Integer expires_in;
}
