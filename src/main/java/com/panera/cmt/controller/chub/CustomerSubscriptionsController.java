package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.chub.CustomerSubscriptionsDTO;
import com.panera.cmt.service.chub.ICustomerSubscriptionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(value = "Customer CustomerSubscription Controller", description = "Manages Customer Subscriptions", produces = "application/json")
@RequestMapping(value = "/api/v1")
@RestController
@Slf4j
public class CustomerSubscriptionsController extends BaseCustomerHubController {

    private ICustomerSubscriptionService customerSubscriptionService;

    @Autowired
    public CustomerSubscriptionsController(ICustomerSubscriptionService customerSubscriptionService) {
        this.customerSubscriptionService = customerSubscriptionService;
    }

    @ApiOperation(value = "getUserSubscriptions, Required Authority: ADMIN, CBSS, CBSS_MANAGER, PROD_SUPPORT")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CustomerSubscriptionsDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "/customer/{customerId}/subscriptions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getSubscriptions(@PathVariable("customerId") Long customerId) {
        return customerSubscriptionService.getSubscriptions(customerId)
                .map(CustomerSubscriptionsDTO::fromEntity)
                .map(preferences -> new ResponseEntity<>(preferences, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "updateSubscriptions, Required Authority: ADMIN, CBSS, CBSS_MANAGER, PROD_SUPPORT")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/customer/{customerId}/subscriptions", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateSubscriptions(@PathVariable("customerId") Long customerId,
                                                 @RequestBody CustomerSubscriptionsDTO subscriptions) {
        return customerSubscriptionService.updateSubscriptions(customerId, subscriptions.toEntity())
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
