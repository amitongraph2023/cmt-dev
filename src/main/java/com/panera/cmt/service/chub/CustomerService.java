package com.panera.cmt.service.chub;

import com.panera.cmt.dto.chub.CustomerDTO;
import com.panera.cmt.dto.proxy.chub.Customer;
import com.panera.cmt.dto.proxy.chub.CustomerDetails;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.enums.UpdateAccountStatusAction;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_CUSTOMER;
import static com.panera.cmt.util.SharedUtils.isNull;

@Service
@Slf4j
public class CustomerService extends BaseCustomerHubService implements ICustomerService {

    @Override
    public Optional<Customer> getCustomer(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "getCustomer", String.format("Getting customer for id=%d", id));

        return Optional.ofNullable(doGet(Customer.class, stopWatch, ChubEndpoints.CUSTOMER_BY_ID, id));
    }

    @Override
    public Optional<CustomerDetails> getCustomerDetails(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "getCustomerDetails", String.format("Getting customer details for id=%d", id));

        return Optional.ofNullable(doGet(CustomerDetails.class, stopWatch, ChubEndpoints.CUSTOMER_DETAILS, id));
    }

    @Override
    public Boolean updateAccountStatus(Long id, UpdateAccountStatusAction action) {
        if (id == null || action == null) {
            return null;
        }
            Map<String, String> body = new HashMap<>();

            if (action.equals(UpdateAccountStatusAction.REINSTATE)) {
                body.put("reasonTypeCode", "REINSTATE");
            }

            if (action.equals(UpdateAccountStatusAction.SUSPEND)) {
                body.put("reasonTypeCode", "SUSPEND");
            }

            if (action.equals(UpdateAccountStatusAction.TERMINATE)) {
                body.put("reason", "FRAUD");
            }

            if (action.equals(UpdateAccountStatusAction.PROTECT)) {
                body.put("reason", "PROTECT");
            }

            StopWatch stopWatch = new StopWatch(log, "updateAccountStatus", String.format("Updating status of customer id=%d, action=%s", id, action));
            return (doPost(String.class, stopWatch, createAudit(ActionType.UPDATE, id, body), body, ChubEndpoints.MANAGE_ACCOUNT, id, action.getRouteParamName())).getStatus().is2xxSuccessful();
    }

    @Override
    public Optional<ResponseHolder<Customer>> updateCustomer(Long id, Customer updatedCustomer) {
        if (isNull(id, updatedCustomer)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "updateCustomer", String.format("Updating customer for id=%d", id));

        return Optional.ofNullable(doPut(Customer.class, stopWatch, createAudit(ActionType.UPDATE, id, updatedCustomer), CustomerDTO.fromEntity(updatedCustomer), ChubEndpoints.CUSTOMER_BY_ID, id));
    }

    @Override
    protected String getSubjectName() {
        return AUDIT_SUBJECT_CUSTOMER;
    }
}
