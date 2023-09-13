package com.panera.cmt.service.chub;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.panera.cmt.dto.proxy.chub.SearchCustomer;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.enums.CustomerSearchType;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.panera.cmt.util.SharedUtils.isAnyNull;

@Service
@Slf4j
public class CustomerSearchService extends BaseCustomerHubService implements ICustomerSearchService {

    @Override
    public ResponseHolder<List<SearchCustomer>> searchCustomer(CustomerSearchType type, String value) {
        if (isAnyNull(type, value)) {
            ResponseHolder<List<SearchCustomer>> responseHolder = new ResponseHolder<>();
            responseHolder.setEntity(new ArrayList<>());
            return responseHolder;
        }

        StopWatch stopWatch = new StopWatch(log, "searchCustomer", String.format("Searching for user search type=%s, value=%s", type, value));

        ResponseHolder<String> response;
        List<SearchCustomer> customers = new ArrayList<>();
        if (type.equals(CustomerSearchType.username)) {
            response = doGetResponse(String.class, stopWatch, ChubEndpoints.USERNAME_LOOKUP, value);

            if (response.getEntity() != null) {
                Long customerId = new Gson().fromJson(response.getEntity(), JsonObject.class).get("customerId").getAsLong();

                String customerResponse = doGet(String.class, stopWatch, ChubEndpoints.CUSTOMER_DETAILS_FOR_SEARCH, customerId);

                customers.add(SearchCustomer.detailsFromJsonObject(new Gson().fromJson(customerResponse, JsonObject.class)));
            } else {
                response = new ResponseHolder<>();
                response.setStatus(HttpStatus.OK);
            }
        } else if (type.equals(CustomerSearchType.customerid)) {
            response = new ResponseHolder<>();
            response.setStatus(HttpStatus.OK);
            Long customerId = Long.valueOf(value);

            if (customerId != null) {
                response.setStatus(HttpStatus.OK);
                response.setErrors(null);
                String customerResponse = doGet(String.class, stopWatch, ChubEndpoints.CUSTOMER_DETAILS_FOR_SEARCH, customerId);
                response.setEntity(customerResponse);
                customers.add(SearchCustomer.detailsFromJsonObject(new Gson().fromJson(customerResponse, JsonObject.class)));
            }
        } else {
            response = doGetResponse(String.class, stopWatch, ChubEndpoints.CUSTOMER_SEARCH, type, value);

            if (response.getEntity() != null) {
                for (JsonObject customer : new Gson().fromJson(response.getEntity(), JsonObject[].class)) {
                    customers.add(SearchCustomer.fromJsonObject(customer));
                }
            }
        }

        ResponseHolder<List<SearchCustomer>> responseHolder = new ResponseHolder<>();
        responseHolder.setErrors(response.getErrors());
        responseHolder.setStatus(response.getStatus());
        if (response.getEntity() != null) {
            responseHolder.setEntity(customers.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }

        if (responseHolder.getEntity() == null) {
            responseHolder.setEntity(new ArrayList<>());
        }

        return responseHolder;
    }

    @Override
    protected String getSubjectName() {
        return "customerSearch";
    }
}
