package com.panera.cmt.dto.sso;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpoofButtonDTO {

    private String name;

    private String text;

    private String unit;

    private String url;

    private String catVisible;
}
