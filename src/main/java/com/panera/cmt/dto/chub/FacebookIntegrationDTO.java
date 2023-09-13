package com.panera.cmt.dto.chub;

import com.panera.cmt.dto.proxy.chub.FacebookIntegration;
import com.panera.cmt.util.DTOConverter;
import lombok.Data;

@Data
public class FacebookIntegrationDTO {

    private String facebookId;

    public static FacebookIntegrationDTO fromEntity(FacebookIntegration entity) {
        return DTOConverter.convert(new FacebookIntegrationDTO(), entity);
    }
}
