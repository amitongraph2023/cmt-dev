package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.chub.SearchCustomerDTO;
import com.panera.cmt.dto.proxy.chub.SearchCustomer;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.CustomerSearchType;
import com.panera.cmt.service.chub.ICustomerSearchService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Api(value = "Customer Search Controller", description = "Searches Customers", produces = "application/json")
@RequestMapping(value = "/api/v1/customer/search")
@RestController
@Slf4j
public class CustomerSearchController extends BaseCustomerHubController {

    private ICustomerSearchService customerSearchService;

    @Autowired
    public CustomerSearchController(ICustomerSearchService customerSearchService) {
        this.customerSearchService = customerSearchService;
    }

    @ApiOperation(value = "searchCustomers, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Sales Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SearchCustomerDTO.class, responseContainer = "Page")
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> searchCustomers(@ApiParam("The search type")
                                                      @RequestParam(value = "type") CustomerSearchType type,
                                                  @ApiParam("The search value")
                                                      @RequestParam(value = "value") String value) {
        ResponseHolder<List<SearchCustomer>> response = customerSearchService.searchCustomer(type, value);

        if (response.getStatus().equals(HttpStatus.NOT_FOUND)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        } else if (response.getStatus().is4xxClientError()) {
            return new ResponseEntity<>(response.getErrors(), HttpStatus.NOT_ACCEPTABLE);
        } else {
            return new ResponseEntity<>(response.getEntity()
                    .stream()
                    .map(SearchCustomerDTO::fromEntity)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()), HttpStatus.OK);
        }
    }
}
