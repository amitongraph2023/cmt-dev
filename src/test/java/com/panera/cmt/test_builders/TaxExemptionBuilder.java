package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.TaxExemption;

import java.util.UUID;

public class TaxExemptionBuilder extends BaseObjectBuilder<TaxExemption> {

    private String company = UUID.randomUUID().toString();
    private String state = "MO";
    private String country = "United States";

    @Override
    TaxExemption getTestClass() {
        return new TaxExemption();
    }
}
