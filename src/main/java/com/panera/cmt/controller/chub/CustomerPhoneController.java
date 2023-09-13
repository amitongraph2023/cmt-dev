package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.chub.PhoneDTO;
import com.panera.cmt.service.chub.ICustomerPhoneService;
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

@Api(value = "Customer Phone Controller", description = "Manages Customer Phones", produces = "application/json")
@RequestMapping(value = "/api/v1/customer/{customerId}/phone")
@RestController
@Slf4j
public class CustomerPhoneController extends BaseCustomerHubController {

    private ICustomerPhoneService customerPhoneService;

    @Autowired
    public CustomerPhoneController(ICustomerPhoneService customerPhoneService) {
        this.customerPhoneService = customerPhoneService;
    }

    @ApiOperation(value = "addPhone, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> addPhone(@PathVariable("customerId") Long id,
                                      @RequestBody PhoneDTO dto) {
        return customerPhoneService.createPhone(id, dto.toEntity())
                .map(response -> returnResponse(response, HttpStatus.CREATED))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ApiOperation(value = "deletePhone, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/{phoneId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> deletePhone(@PathVariable("customerId") Long id,
                                         @PathVariable("phoneId") Long phoneId) {
        return customerPhoneService.deletePhone(id, phoneId)
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ApiOperation(value = "getPhone, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = PhoneDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "/{phoneId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getPhone(@PathVariable("customerId") Long customerId,
                                      @PathVariable("phoneId") Long phoneId) {
        return customerPhoneService.getPhone(customerId, phoneId)
                .map(PhoneDTO::fromEntity)
                .map(phone -> new ResponseEntity<>(phone, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "getPhones, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = PhoneDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getPhones(@PathVariable("customerId") Long customerId) {
        return customerPhoneService.getPhones(customerId)
                .map(entities -> entities.stream()
                        .map(PhoneDTO::fromEntity)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .map(phones -> new ResponseEntity<>(phones, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "setDefault, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/{phoneId}/default", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> setDefault(@PathVariable("customerId") Long id,
                                        @PathVariable("phoneId") Long phoneId) {
        return customerPhoneService.setDefault(id, phoneId)
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ApiOperation(value = "updatePhone, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/{phoneId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updatePhone(@PathVariable("customerId") Long id,
                                         @PathVariable("phoneId") Long phoneId,
                                         @RequestBody PhoneDTO dto) {
        return customerPhoneService.updatePhone(id, phoneId, dto.toEntity())
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
