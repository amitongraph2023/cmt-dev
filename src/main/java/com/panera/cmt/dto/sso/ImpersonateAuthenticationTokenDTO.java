package com.panera.cmt.dto.sso;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@ApiModel(value="ImpersonateAuthenticationToken", description="SSO impersonation session information")
@Data
public class ImpersonateAuthenticationTokenDTO {
    private Long customerId;
    private String username;
    private String accessToken;
    private Date loginDate;
    private Date expirationDate;
    private String originSourceType;
    private String channelType;
    private String loginType;
    private String proxyUser;
}
