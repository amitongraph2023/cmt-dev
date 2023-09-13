package com.panera.cmt.dto.chub;

import com.panera.cmt.dto.proxy.chub.TaxExemption;
import com.panera.cmt.util.DTOConverter;
import lombok.Data;

@Data
public class TaxExemptionDTO {

    private String company;
    private String state;
    private String country;

    public static TaxExemptionDTO fromEntity(TaxExemption entity) {
        return DTOConverter.convert(new TaxExemptionDTO(), entity);
    }
}
