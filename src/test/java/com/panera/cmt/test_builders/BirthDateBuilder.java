package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.BirthDate;

public class BirthDateBuilder extends BaseObjectBuilder<BirthDate> {

    private String birthDay = "01";
    private String birthMonth = "01";

    public BirthDateBuilder withBirthDay(String birthDay) {
        this.birthDay = birthDay;
        return this;
    }

    public BirthDateBuilder withBirthMonth(String birthMonth) {
        this.birthMonth = birthMonth;
        return this;
    }

    @Override
    BirthDate getTestClass() {
        return new BirthDate();
    }
}
