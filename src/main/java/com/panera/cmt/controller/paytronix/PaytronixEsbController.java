package com.panera.cmt.controller.paytronix;

import com.panera.cmt.service.paytronix.IPaytronixEsbService;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Api(value = "Paytronix Esb Controller", description = "Paytronix Esb functionality", produces = "application/json")
@RequestMapping(value = "/api/v1/paytronixEsb")
@RestController
@Slf4j
public class PaytronixEsbController extends BasePaytronixController {

    private IPaytronixEsbService paytronixEsbService;

    @Autowired
    public PaytronixEsbController(IPaytronixEsbService paytronixEsbService){
        this.paytronixEsbService = paytronixEsbService;
    }

    @ApiOperation(value = "getBalance")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Object.class)
    })
    @RequestMapping(value = "/{cardNumber}/balance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getBalance(@PathVariable String cardNumber){
        return paytronixEsbService.getBalance(cardNumber)
                .map(response -> response.getBody())
                .map(data -> new ResponseEntity<>(data, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "getTransactionHistory")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Object.class)
    })
    @RequestMapping(value = "/{cardNumber}/transactionHistory", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getTransactionHistory (@PathVariable String cardNumber){
        return getTransactionHistory(cardNumber, LocalDate.now().minusDays(365).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    @ApiOperation(value = "getTransactionHistory")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Object.class)
    })
    @RequestMapping(value = "/{cardNumber}/transactionHistory/{startDate}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getTransactionHistory (@PathVariable String cardNumber, @PathVariable String startDate)
    {
        return paytronixEsbService.getTransactionHistory(cardNumber, startDate)
                .map(response -> response.getBody())
                .map(data -> new ResponseEntity<>(data, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "getWalletCodes")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Object.class)
    })
    @RequestMapping(value = "/walletCodes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getWallets(){
        return paytronixEsbService.getWalletCodes()
                .map(response -> response.getBody())
                .map(wallets -> new ResponseEntity<>(wallets, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}