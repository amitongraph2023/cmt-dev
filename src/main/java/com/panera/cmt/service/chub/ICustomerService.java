package com.panera.cmt.service.chub;

import com.panera.cmt.dto.proxy.chub.Customer;
import com.panera.cmt.dto.proxy.chub.CustomerDetails;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.UpdateAccountStatusAction;

import java.util.Optional;

public interface ICustomerService {

    Optional<Customer> getCustomer(Long id);

    Optional<CustomerDetails> getCustomerDetails(Long id);

    Boolean updateAccountStatus(Long id, UpdateAccountStatusAction action);

    Optional<ResponseHolder<Customer>> updateCustomer(Long id, Customer updatedCustomer);
}
