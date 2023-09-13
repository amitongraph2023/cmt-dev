package com.panera.cmt.dto.chub;

import com.panera.cmt.dto.proxy.chub.SearchCustomer;
import com.panera.cmt.util.DTOConverter;
import lombok.Data;

@Data
public class SearchCustomerDTO {

    private Long customerId;
    private String username;
    private String firstName;
    private String lastName;
    private String defaultEmail;
    private String defaultPhone;

    public static SearchCustomerDTO fromEntity(SearchCustomer entity) {
        return DTOConverter.convert(new SearchCustomerDTO(), entity);
    }
}
