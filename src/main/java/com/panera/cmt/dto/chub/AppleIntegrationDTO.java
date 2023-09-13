package com.panera.cmt.dto.chub;

import com.panera.cmt.dto.proxy.chub.AppleIntegration;
import com.panera.cmt.util.DTOConverter;
import lombok.Data;

@Data
public class AppleIntegrationDTO {

    private String appleId;

    public static AppleIntegrationDTO fromEntity(AppleIntegration entity){
        return DTOConverter.convert(new AppleIntegrationDTO(), entity);
    }

}