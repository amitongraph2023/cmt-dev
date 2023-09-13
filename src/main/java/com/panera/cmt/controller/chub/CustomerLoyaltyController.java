package com.panera.cmt.controller.chub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.panera.cmt.dto.chub.CardExchangeDTO;
import com.panera.cmt.dto.chub.EmailDTO;
import com.panera.cmt.dto.chub.LoyaltyAccountsDTO;
import com.panera.cmt.dto.chub.LoyaltyDTO;
import com.panera.cmt.dto.chub.LoyaltyRewardsEnabledDTO;
import com.panera.cmt.service.chub.ICustomerLoyaltyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api(value = "Customer Loyalty Controller", description = "Manages Customer Loyalty Information", produces = "application/json")
@RequestMapping(value = "/api/v1/customer/{customerId}/loyalty")
@RestController
@Slf4j
public class CustomerLoyaltyController extends BaseCustomerHubController {

    private ICustomerLoyaltyService customerLoyaltyService;

    @Autowired
    public CustomerLoyaltyController(ICustomerLoyaltyService customerLoyaltyService){
        this.customerLoyaltyService = customerLoyaltyService;
    }

    @ApiOperation(value = "cardExchange, Required Authorities: Admin, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/cardExchange/{existingLoyaltyCard}/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> loyaltyAccountExchange(@PathVariable("customerId") Long customerId,
                                                 @ApiParam("The existing customer loyalty card number")
                                                 @PathVariable(value = "existingLoyaltyCard") String existingLoyaltyCard,
                                                 @ApiParam("Exclude the Paytronix card exchange portion")
                                                 @RequestParam(value = "excludePX") boolean excludePX,
                                                 @RequestBody CardExchangeDTO dto) {
        return customerLoyaltyService.cardExchange(customerId, existingLoyaltyCard, dto, excludePX)
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "updateLoyaltyAccountByCustomerId, Required Authorities: Admin, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = LoyaltyAccountsDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateLoyaltyAccountByCustomerId(@PathVariable("customerId") Long customerId) {
        return customerLoyaltyService.updateLoyalty(customerId)
                .map(loyaltyAccountsDTO -> new ResponseEntity<>(loyaltyAccountsDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

	@ApiOperation(value = "getRewardsEnabledByCustomerId, Required Authorities: Admin, Cbss Supervisor, Prod Support")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = LoyaltyDTO.class),
			@ApiResponse(code = 404, message = "Not Found", response = String.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = String.class) })
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> getRewardsEnabledByCustomerId(@PathVariable("customerId") Long customerId) {
		return customerLoyaltyService.getLoyaltyRewardsEnabled(customerId)
				.map(LoyaltyDTO::fromEntity)
				.map(loyaltyDTO -> new ResponseEntity<>(loyaltyDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@ApiOperation(value = "updateLoyaltyRewardsByCustomerId, Required Authorities: Admin, Cbss Supervisor, Prod Support")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = LoyaltyRewardsEnabledDTO.class),
			@ApiResponse(code = 404, message = "Not Found", response = String.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = String.class) })
	@RequestMapping(value = "/rewards-status", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> updateLoyaltyRewardsByCustomerId(@PathVariable("customerId") Long customerId,
			@RequestBody LoyaltyRewardsEnabledDTO dto) {
		return customerLoyaltyService.updateLoyaltyRewardsEnabled(customerId, dto)
				.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

}