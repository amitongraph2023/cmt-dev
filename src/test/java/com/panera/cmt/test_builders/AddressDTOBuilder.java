package com.panera.cmt.test_builders;

import com.panera.cmt.dto.chub.AddressDTO;

import java.util.Random;
import java.util.UUID;

public class AddressDTOBuilder extends BaseObjectBuilder<AddressDTO> {

    private Long id = new Random().nextLong();
    private String name = UUID.randomUUID().toString();
    private String contactPhone = "5555555554";
    private String phoneExtension = "5555555554";
    private String additionalInfo = UUID.randomUUID().toString();
    private String addressLine1 = UUID.randomUUID().toString();
    private String addressLine2 = UUID.randomUUID().toString();
    private String city = UUID.randomUUID().toString();
    private String state = "MO";
    private String country = "United States";
    private String zip = "123456789";
    private String addressType = "Residential";
    private boolean isDefault = true;

    @Override
    AddressDTO getTestClass() {
        return new AddressDTO();
    }
}
