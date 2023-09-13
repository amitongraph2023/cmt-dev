package com.panera.cmt.dto.chub;

import com.panera.cmt.dto.proxy.chub.EmailVerification;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailVerificationDTO {

    private String emailAddress;


    public static EmailVerificationDTO fromEntity(EmailVerification entity) {
        return EmailVerificationDTO.builder()
                .emailAddress(entity.getEmailAddress())
                .build();
    }

    public EmailVerification toEntity() {
        return EmailVerification.builder()
                .emailAddress(emailAddress)
                .build();
    }
}
