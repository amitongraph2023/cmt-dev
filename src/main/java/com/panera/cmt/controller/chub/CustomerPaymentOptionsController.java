package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.chub.*;
import com.panera.cmt.dto.proxy.chub.ApplePay;
import com.panera.cmt.dto.proxy.chub.CreditCard;
import com.panera.cmt.dto.proxy.chub.GiftCard;
import com.panera.cmt.dto.proxy.chub.PayPal;
import com.panera.cmt.enums.PaymentOptionType;
import com.panera.cmt.service.chub.ICustomerPaymentService;
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

@Api(value = "Customer Payment Options Controller", description = "Manages Customer Payment Options", produces = "application/json")
@RequestMapping(value = "/api/v1/customer/{customerId}/paymentoptions")
@RestController
@Slf4j
public class CustomerPaymentOptionsController extends BaseCustomerHubController {

    private ICustomerPaymentService customerPaymentService;

    @Autowired
    public CustomerPaymentOptionsController(ICustomerPaymentService customerPaymentService) {
        this.customerPaymentService = customerPaymentService;
    }
    @ApiOperation(value = "addGiftCard, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/giftcard", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> addGiftCard(@PathVariable("customerId") Long id,
                                         @RequestBody GiftCardDTO dto) {
        return customerPaymentService.addPaymentOption(id, PaymentOptionType.giftcard, dto.toEntity())
                .map(response -> returnResponse(response, HttpStatus.CREATED))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ApiOperation(value = "deletePaymentOption, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/{type}/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> deletePaymentOption(@PathVariable("customerId") Long customerId,
                                                 @PathVariable("type") PaymentOptionType type,
                                                 @PathVariable("id") String id) {
        return customerPaymentService.deletePaymentOption(customerId, id, type)
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ApiOperation(value = "getApplePay, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Sales Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = PayPalDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "/applepay/{accountNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getApplePay(@PathVariable("customerId") Long customerId,
                                       @PathVariable("accountNumber") String accountNumber) {
        return customerPaymentService.getPaymentOption(customerId, accountNumber, PaymentOptionType.applepay)
                .map(entity -> ApplePayDTO.fromEntity((ApplePay) entity))
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @ApiOperation(value = "getCreditCard, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Sales Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CreditCardDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "/creditcard/{token}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getCreditCard(@PathVariable("customerId") Long customerId,
                                           @PathVariable("token") String token) {
        return customerPaymentService.getPaymentOption(customerId, token, PaymentOptionType.creditcard)
                .map(entity -> CreditCardDTO.fromEntity((CreditCard) entity))
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "getGiftCard, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Sales Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = GiftCardDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "/giftcard/{cardNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getGiftCard(@PathVariable("customerId") Long customerId,
                                         @PathVariable("cardNumber") String cardNumber) {
        return customerPaymentService.getPaymentOption(customerId, cardNumber, PaymentOptionType.giftcard)
                .map(entity -> GiftCardDTO.fromEntity((GiftCard) entity))
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "getPayPal, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Sales Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = PayPalDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "/paypal/{accountNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getPayPal(@PathVariable("customerId") Long customerId,
                                       @PathVariable("accountNumber") String accountNumber) {
        return customerPaymentService.getPaymentOption(customerId, accountNumber, PaymentOptionType.paypal)
                .map(entity -> PayPalDTO.fromEntity((PayPal) entity))
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "getPaymentOptions, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Sales Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = PaymentOptionsDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getPaymentOptions(@PathVariable("customerId") Long customerId) {
        return customerPaymentService.getPaymentOptions(customerId)
                .map(PaymentOptionsDTO::fromEntity)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "updateCreditCard, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/creditcard/{token}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateCreditCard(@PathVariable("customerId") Long id,
                                              @PathVariable("token") String token,
                                              @RequestBody CreditCardDTO dto) {
        return customerPaymentService.updatePaymentOption(id, token, PaymentOptionType.creditcard, dto.toEntity())
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ApiOperation(value = "updateGiftCard, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/giftcard/{cardNumber}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateGiftCard(@PathVariable("customerId") Long id,
                                            @PathVariable("cardNumber") String cardNumber,
                                            @RequestBody GiftCardDTO dto) {
        return customerPaymentService.updatePaymentOption(id, cardNumber, PaymentOptionType.giftcard, dto.toEntity())
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ApiOperation(value = "updatePayPal, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/paypal/{accountNumber}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updatePayPal(@PathVariable("customerId") Long id,
                                          @PathVariable("accountNumber") String accountNumber,
                                          @RequestBody PayPalDTO dto) {
        return customerPaymentService.updatePaymentOption(id, accountNumber, PaymentOptionType.paypal, dto.toEntity())
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
    @ApiOperation(value = "addBonusCard, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/bonuscard", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> addBonusCard(@PathVariable("customerId") Long id,
                                         @RequestBody BonusCardDTO dto) {
        return customerPaymentService.addPaymentOption(id, PaymentOptionType.bonuscard, dto.toEntity())
                .map(response -> returnResponse(response, HttpStatus.CREATED))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
