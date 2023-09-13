package com.panera.cmt.service.chub;

import com.panera.cmt.dto.proxy.chub.SearchCustomer;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.CustomerSearchType;

import java.util.List;

public interface ICustomerSearchService {

    ResponseHolder<List<SearchCustomer>> searchCustomer(CustomerSearchType type, String value);
}
