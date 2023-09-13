package com.panera.cmt.dto.chub;

import com.panera.cmt.dto.proxy.chub.ApplePay;
import com.panera.cmt.util.DTOConverter;
import lombok.Data;

@Data
public class ApplePayDTO {
    private String accountNumber;

    public static ApplePayDTO fromEntity(ApplePay entity) {
        return DTOConverter.convert(new ApplePayDTO(), entity);
    }

    public ApplePay toEntity() {
        ApplePay entity = new ApplePay();
        entity.setAccountNumber(accountNumber);

        return entity;
    }

}