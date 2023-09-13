package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.chub.EmailDTO;
import com.panera.cmt.dto.chub.EmailVerificationDTO;
import com.panera.cmt.service.chub.ICustomerEmailService;
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

@Api(value = "Customer Email Controller", description = "Manages Customer Emails", produces = "application/json")
@RequestMapping(value = "/api/v1/customer/{customerId}/email")
@RestController
@Slf4j
public class CustomerEmailController extends BaseCustomerHubController {

    private ICustomerEmailService customerEmailService;

    @Autowired
    public CustomerEmailController(ICustomerEmailService customerEmailService) {
        this.customerEmailService = customerEmailService;
    }

    @ApiOperation(value = "addEmail, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses( value = {
            @ApiResponse(code = 201, message = "Created", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> addEmail(@PathVariable("customerId") Long id,
                                      @RequestBody EmailDTO dto) {
        return customerEmailService.createEmail(id, dto.toEntity())
                .map(response -> returnResponse(response, HttpStatus.CREATED))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @ApiOperation(value = "getEmail, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = EmailDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "/{emailId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getEmail(@PathVariable("customerId") Long customerId,
                                      @PathVariable("emailId") Long emailId) {
        return customerEmailService.getEmail(customerId, emailId)
                .map(EmailDTO::fromEntity)
                .map(email -> new ResponseEntity<>(email, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "getEmails, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = EmailDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getEmails(@PathVariable("customerId") Long customerId) {
        return customerEmailService.getEmails(customerId)
                .map(entities -> entities.stream()
                        .map(EmailDTO::fromEntity)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .map(emails -> new ResponseEntity<>(emails, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "setDefault, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/{emailId}/default", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> setDefault(@PathVariable("customerId") Long id,
                                        @PathVariable("emailId") Long emailId) {
        return customerEmailService.setDefault(id, emailId)
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ApiOperation(value = "updateEmail, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/{emailId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateEmail(@PathVariable("customerId") Long id,
                                         @PathVariable("emailId") Long emailId,
                                         @RequestBody EmailDTO dto) {
        return customerEmailService.updateEmail(id, emailId, dto.toEntity())
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ApiOperation(value = "resendValidationEmail, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/resendVerificationEmail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> resendVerificationEmail(@PathVariable("customerId") Long id,
                                         @RequestBody EmailVerificationDTO dto) {
        return customerEmailService.resendVerificationEmail(id, dto.toEntity())
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
