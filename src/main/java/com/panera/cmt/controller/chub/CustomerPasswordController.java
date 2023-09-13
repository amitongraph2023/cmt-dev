package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.chub.PasswordDTO;
import com.panera.cmt.dto.chub.SetPasswordDTO;
import com.panera.cmt.dto.chub.WotdDTO;
import com.panera.cmt.service.chub.ICustomerPasswordService;
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

@Api(value = "Customer Password Controller", description = "Manages Customer Password", produces = "application/json")
@RequestMapping(value = "/api/v1/customer/{customerId}/password")
@RestController
@Slf4j
public class CustomerPasswordController extends BaseCustomerHubController {

    private ICustomerPasswordService customerPasswordService;

    @Autowired
    public CustomerPasswordController(ICustomerPasswordService customerPasswordService) {
        this.customerPasswordService = customerPasswordService;
    }

    @ApiOperation(value = "adminSetPassword, Required Authority: ADMIN, CBSS, CBSS_MANAGER, PROD_SUPPORT")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/admin/set", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> adminSetPassword(@PathVariable("customerId") Long id,
                                              @RequestBody SetPasswordDTO dto) {
        customerPasswordService.adminSetPassword(id, dto.getPassword());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).contentLength(0L).build();
    }

    @ApiOperation(value = "resetPassword, Required Authority: ADMIN, CBSS, CBSS_MANAGER, PROD_SUPPORT")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = PasswordDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/reset", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> resetPassword(@PathVariable("customerId") Long id) {
        return customerPasswordService.generatePassword(id)
                .map(PasswordDTO::fromEntity)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "sendResetPasswordEmail, Required Authority: ADMIN, CBSS, CBSS_MANAGER, PROD_SUPPORT")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/sendreset", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> sendResetPasswordEmail(@PathVariable("customerId") Long id) {
        customerPasswordService.sendResetPasswordEmail(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).contentLength(0L).build();
    }


    @ApiOperation(value = "getWordOfTheDay, Required Authority: ADMIN, CBSS_MANAGER, PROD_SUPPORT")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = WotdDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/wotd", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getWordOfTheDay(@PathVariable("customerId") Long id) {
        return customerPasswordService.getWordOfTheDay()
                .map(wotd -> new ResponseEntity<>(wotd, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
