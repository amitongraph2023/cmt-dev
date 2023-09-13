package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.chub.CustomerDTO;
import com.panera.cmt.dto.chub.CustomerDetailsDTO;
import com.panera.cmt.enums.UpdateAccountStatusAction;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.service.chub.ICustomerService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(value = "Customer Controller", description = "Manages Customer Information", produces = "application/json")
@RequestMapping(value = "/api/v1/customer/{customerId}")
@RestController
@Slf4j
public class CustomerController extends BaseCustomerHubController {

    private ICustomerService customerService;

    @Autowired
    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    @ApiOperation(value = "getCustomer, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Sales Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CustomerDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getCustomer(@PathVariable("customerId") Long customerId) {
        return customerService.getCustomer(customerId)
                .map(CustomerDTO::fromEntity)
                .map(customer -> new ResponseEntity<>(customer, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "getCustomerDetails, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Sales Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CustomerDetailsDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "/details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getCustomerDetails(@PathVariable("customerId") Long customerId) {
        return customerService.getCustomerDetails(customerId)
                .map(customerDetails -> CustomerDetailsDTO.fromEntity(customerDetails, AuthenticatedUserManager.canViewAllDetails()))
                .map(customerDetailsDTO -> new ResponseEntity<>(customerDetailsDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "updateAccountStatus, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "No Content", response = String.class)
    })
    @RequestMapping(value = "/status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateAccountStatus(@PathVariable("customerId") Long customerId,
                                                 @ApiParam("The action to perform to this account's status")
                                                    @RequestParam(value = "action") UpdateAccountStatusAction action) {
        Boolean response = customerService.updateAccountStatus(customerId, action);

        if(response){
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else if (response != null){
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiOperation(value = "updateCustomer, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateCustomer(@PathVariable("customerId") Long customerId,
                                            @RequestBody CustomerDTO dto) {
        return customerService.updateCustomer(customerId, dto.toEntity())
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

}
