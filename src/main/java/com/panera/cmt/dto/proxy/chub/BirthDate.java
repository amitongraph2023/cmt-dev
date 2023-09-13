package com.panera.cmt.dto.proxy.chub;

import lombok.Data;

import static com.panera.cmt.util.SharedUtils.isNull;

@Data
public class BirthDate {

    private String birthDay;
    private String birthMonth;

    public String getBirthDate() {
        if (isNull(birthDay, birthMonth)) {
            return null;
        }

        return String.format("%s/%s", birthDay, birthMonth);
    }
}
