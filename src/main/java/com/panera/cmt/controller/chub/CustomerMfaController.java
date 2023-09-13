package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.chub.MfaDTO;
import com.panera.cmt.service.chub.ICustomerMfaService;
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


@Api(value = "Customer MFA Controller", description = "Manages Customer MFA Information", produces = "application/json")
@RequestMapping(value = "/api/v1/customer/{customerId}/mfa")
@RestController
@Slf4j
public class CustomerMfaController extends BaseCustomerHubController {

    private ICustomerMfaService customerMfaService;

    @Autowired
    public CustomerMfaController(ICustomerMfaService customerMfaService) {
        this.customerMfaService = customerMfaService;
    }

    @ApiOperation(value = "disableSmsMfa, Required Authorities: Admin, Cbss, Cbss Supervisor, Security, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/sms", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> disableSmsMfa(@PathVariable("customerId") Long id) {
        return customerMfaService.disableSmsMfa(id)
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ApiOperation(value = "getMfas, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Read-Only, Security")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = MfaDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getMfas(@PathVariable("customerId") Long customerId) {
        return customerMfaService.getMfas(customerId)
                .map(entities -> entities.stream()
                        .map(MfaDTO::fromEntity)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .map(mfas -> new ResponseEntity<>(mfas, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}