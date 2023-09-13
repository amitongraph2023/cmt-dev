package com.panera.cmt.dto.proxy.chub;

import javax.persistence.Column;

import lombok.Data;

@Data
public class CampusCard {

    private String campusCardToken;
    private String institutionLongName;
    private String institutionShortName;
    private String providerName;
    private String programName;
    private String customerCampusCardName;
    private String expirationDate;
    private String postalCode;

}
