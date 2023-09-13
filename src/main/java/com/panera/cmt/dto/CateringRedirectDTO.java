package com.panera.cmt.dto;

import lombok.Data;

@Data
public class CateringRedirectDTO {
    private boolean isEnabled;
    private String redirectUrl;
}
