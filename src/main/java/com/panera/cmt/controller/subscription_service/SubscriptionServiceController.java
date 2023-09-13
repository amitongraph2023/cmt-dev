package com.panera.cmt.controller.subscription_service;

import com.panera.cmt.controller.BaseController;
import com.panera.cmt.dto.proxy.subscription_service.SubscriptionPrograms;
import com.panera.cmt.dto.subscription_service.CoffeeSubscriptionUsageDTO;
import com.panera.cmt.dto.subscription_service.SubscriptionServiceCoffeeUsageResultsDTO;
import com.panera.cmt.dto.subscription_service.SubscriptionServiceResultsDTO;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.GiftCoffeeSearchType;
import com.panera.cmt.service.subscrption_service.ISubscriptionService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Api(value = "Subscription Service Coffee Subscription Controller", description = "Searches Customers", produces = "application/json")
@RequestMapping(value = "/api/v1/subscriptionService")
@RestController
@Slf4j
public class SubscriptionServiceController extends BaseController {

    private ISubscriptionService subscriptionService;

    @Autowired
    public SubscriptionServiceController(ISubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @ApiOperation(value = "cancelCoffeeSubscription, Required Authorities: Admin,Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Boolean.class)
    })
    @RequestMapping(value = "/cancel/{customerId}/{programId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> cancelCoffeeSubscription(
            @PathVariable(value = "customerId") Long customerId,
            @PathVariable(value = "programId") Long programId
    ) {
        return subscriptionService.cancelCoffeeSubscriotion(customerId, programId)
                .map(coffeeSubscriptionUsages -> new ResponseEntity<>(coffeeSubscriptionUsages, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "searchCustomers, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Sales Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CoffeeSubscriptionUsageDTO.class)
    })
    @RequestMapping(value = "/coffeeSubscriptionUsage/{customerId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> searchCoffeeUsage(
                                                           @PathVariable(value = "customerId") Long customerId) {
        return subscriptionService.getCustomerCoffeeSubscriptionUsage(customerId)
                .map(entities -> entities.stream()
                .map(CoffeeSubscriptionUsageDTO::fromEntity)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .map(coffeeSubscriptionUsages -> new ResponseEntity<>(coffeeSubscriptionUsages, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "searchCustomers, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Sales Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SubscriptionServiceResultsDTO.class)
    })
    @RequestMapping(value = "/giftcoffeesub/{searchType}/{searchTerm}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> searchCoffeeGiftSubscriptions(@ApiParam("The search type")
                                                           @PathVariable(value = "searchType") GiftCoffeeSearchType searchType,
                                                           @PathVariable(value = "searchTerm") String searchTerm) {
        return subscriptionService.getGiftCoffeeSubscriptions(searchType, searchTerm)
                .map(SubscriptionServiceResultsDTO::fromEntity)
                .map(subscriptionServiceResultsDTO -> new ResponseEntity<>(subscriptionServiceResultsDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
