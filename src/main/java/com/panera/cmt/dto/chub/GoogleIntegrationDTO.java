package com.panera.cmt.dto.chub;

import com.panera.cmt.dto.proxy.chub.GoogleIntegration;
import com.panera.cmt.util.DTOConverter;
import lombok.Data;

@Data
public class GoogleIntegrationDTO {

    private String googleId;

    public static GoogleIntegrationDTO fromEntity(GoogleIntegration entity) {
        return DTOConverter.convert(new GoogleIntegrationDTO(), entity);
    }
}
