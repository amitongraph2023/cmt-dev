package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.chub.AddressDTO;
import com.panera.cmt.service.chub.ICustomerAddressService;
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

import java.util.Objects;
import java.util.stream.Collectors;

@Api(value = "Customer Address Controller", description = "Manages Customer Addresses", produces = "application/json")
@RequestMapping(value = "/api/v1/customer/{customerId}/address")
@RestController
@Slf4j
public class CustomerAddressController extends BaseCustomerHubController {

    private ICustomerAddressService customerAddressService;

    @Autowired
    public CustomerAddressController(ICustomerAddressService customerAddressService) {
        this.customerAddressService = customerAddressService;
    }

    @ApiOperation(value = "addAddress, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> addAddress(@PathVariable("customerId") Long id,
                                        @RequestBody AddressDTO dto) {
        return customerAddressService.createAddress(id, dto.toEntity())
                .map(response -> returnResponse(response, HttpStatus.CREATED))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ApiOperation(value = "deleteAddress, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/{addressId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> deleteAddress(@PathVariable("customerId") Long id,
                                           @PathVariable("addressId") Long addressId) {
        return customerAddressService.deleteAddress(id, addressId)
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ApiOperation(value = "getAddress, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = AddressDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "/{addressId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getAddress(@PathVariable("customerId") Long customerId,
                                        @PathVariable("addressId") Long addressId) {
        return customerAddressService.getAddress(customerId, addressId)
                .map(AddressDTO::fromEntity)
                .map(address -> new ResponseEntity<>(address, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "getAddresses, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = AddressDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getAddresses(@PathVariable("customerId") Long customerId) {
        return customerAddressService.getAddresses(customerId)
                .map(entities -> entities.stream()
                        .map(AddressDTO::fromEntity)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .map(addresses -> new ResponseEntity<>(addresses, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "updateAddress, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/{addressId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateAddress(@PathVariable("customerId") Long id,
                                           @PathVariable("addressId") Long addressId,
                                           @RequestBody AddressDTO dto) {
        return customerAddressService.updateAddress(id, addressId, dto.toEntity())
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
