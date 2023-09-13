package com.panera.cmt.dto.chub;

import com.panera.cmt.dto.proxy.chub.PayPal;
import com.panera.cmt.util.DTOConverter;
import lombok.Data;

@Data
public class PayPalDTO {

    private String accountNumber;
    private String username;

    public static PayPalDTO fromEntity(PayPal entity) {
        return DTOConverter.convert(new PayPalDTO(), entity);
    }

    public PayPal toEntity() {
        PayPal entity = new PayPal();
        entity.setAccountNumber(accountNumber);
        entity.setUsername(username);

        return entity;
    }
}
