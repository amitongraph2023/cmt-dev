package com.panera.cmt.dto.proxy.chub;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panera.cmt.serializer.ChubDateTimeDeserializer;
import com.panera.cmt.serializer.ChubDateTimeSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class CorporateCateringAccount {

    private Long orgNumber;
    private String ccaNumber;

    @JsonSerialize(using = ChubDateTimeSerializer.class)
    @JsonDeserialize(using = ChubDateTimeDeserializer.class)
    private Date clientStartDate;

    @JsonSerialize(using = ChubDateTimeSerializer.class)
    @JsonDeserialize(using = ChubDateTimeDeserializer.class)
    private Date clientEndDate;

    @JsonSerialize(using = ChubDateTimeSerializer.class)
    @JsonDeserialize(using = ChubDateTimeDeserializer.class)
    private Date orgStartDate;

    @JsonSerialize(using = ChubDateTimeSerializer.class)
    @JsonDeserialize(using = ChubDateTimeDeserializer.class)
    private Date orgEndDate;

    private String ccaBillingName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private Boolean poRequired;
    private Boolean onlineEnabled;
}
