package com.panera.cmt.dto.proxy.chub;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailVerification {

    private String emailAddress;

}
