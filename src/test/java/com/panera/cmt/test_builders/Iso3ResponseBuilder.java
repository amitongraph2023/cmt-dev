package com.panera.cmt.test_builders;

import com.panera.cmt.entity.Iso3Response;
import org.apache.commons.codec.binary.Base64;

import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class Iso3ResponseBuilder extends BaseObjectBuilder<Iso3Response> {
    
    private String access_token = UUID.randomUUID().toString();
    private String refresh_token = UUID.randomUUID().toString();
    private String scope = "openid";
    private String id_token = encodeString(UUID.randomUUID().toString());
    private String token_type = "Bearer";
    private Integer expires_in = 3600;

    public Iso3ResponseBuilder withAccess_token(String access_token) {
        this.access_token = access_token;
        return this;
    }

    public Iso3ResponseBuilder withRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
        return this;
    }

    public Iso3ResponseBuilder withScope(String scope) {
        this.scope = scope;
        return this;
    }

    public Iso3ResponseBuilder withId_token(String id_token) {
        this.id_token = encodeString(id_token);
        return this;
    }

    public Iso3ResponseBuilder withId_token(Map id_token_map) {
        this.id_token = encodeString(id_token_map.toString());
        return this;
    }

    public Iso3ResponseBuilder withToken_type(String token_type) {
        this.token_type = token_type;
        return this;
    }

    public Iso3ResponseBuilder withExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
        return this;
    }

    @Override
    Iso3Response getTestClass() {
        return new Iso3Response();
    }

    private String encodeString(String raw) {
        return (raw != null) ? randomAlphanumeric(19) + "." + new String(new Base64(true).encode(raw.getBytes())) : null;
    }
}
