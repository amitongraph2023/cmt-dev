package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.chub.GeneralPreferenceDTO;
import com.panera.cmt.dto.chub.PersonGeneralPreferenceDTO;
import com.panera.cmt.service.chub.ICustomerPreferencesService;
import com.panera.cmt.service.chub.ICustomerService;
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

import java.util.List;
import java.util.stream.Collectors;

@Api(value = "Customer Controller", description = "Manages Customer Information", produces = "application/json")
@RequestMapping(value = "/api/v1/customer/{customerId}/userpreferences")
@RestController
@Slf4j
public class CustomerPreferencesController extends BaseCustomerHubController {

    private ICustomerPreferencesService customerPreferencesService;
    private ICustomerService customerService;

    @Autowired
    public CustomerPreferencesController(ICustomerPreferencesService customerPreferencesService, ICustomerService customerService) {
        this.customerPreferencesService = customerPreferencesService;
        this.customerService = customerService;
    }

    @ApiOperation(value = "getUserPreferences, Required Authority: ADMIN, CBSS, CBSS_MANAGER, PROD_SUPPORT")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = GeneralPreferenceDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getUserPreferences(@PathVariable("customerId") Long customerId) {
        if (this.customerService.getCustomer(customerId).isPresent()){
            return customerPreferencesService.getPreferences(customerId)
                    .map(GeneralPreferenceDTO::fromEntity)
                    .map(preferences -> new ResponseEntity<>(preferences, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(new GeneralPreferenceDTO(), HttpStatus.OK));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "updateFoodPreferences, Required Authority: ADMIN, CBSS, CBSS_MANAGER, PROD_SUPPORT")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/dietary", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateFoodPreferences(@PathVariable("customerId") Long customerId,
                                                   @RequestBody List<PersonGeneralPreferenceDTO> preferences) {
        return customerPreferencesService.updateFoodPreferences(customerId, preferences.stream()
                    .map(PersonGeneralPreferenceDTO::toEntity)
                    .collect(Collectors.toList()))
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ApiOperation(value = "updateGatherPreferences, Required Authority: ADMIN, CBSS, CBSS_MANAGER, PROD_SUPPORT")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/gather", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateGatherPreferences(@PathVariable("customerId") Long customerId,
                                                     @RequestBody PersonGeneralPreferenceDTO preference) {
        return customerPreferencesService.updateGatherPreference(customerId, preference.toEntity())
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ApiOperation(value = "updateUserPreferences, Required Authority: ADMIN, CBSS, CBSS_MANAGER, PROD_SUPPORT")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateUserPreferences(@PathVariable("customerId") Long customerId,
                                                   @RequestBody GeneralPreferenceDTO preferences) {
        return customerPreferencesService.updateUserPreferences(customerId, preferences.toEntity())
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
