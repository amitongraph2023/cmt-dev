package com.panera.cmt.dto.lmt;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@AllArgsConstructor
@Data
public class LTODTO {
    private String specialCode;
    private String discount;
    private String businessUnit;
    private Date startDateTime;
    private Date endDateTime;
    private Set<CafeDTO> cafes;
}
