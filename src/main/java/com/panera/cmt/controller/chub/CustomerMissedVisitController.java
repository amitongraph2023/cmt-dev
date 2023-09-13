package com.panera.cmt.controller.chub;

import com.panera.cmt.service.chub.ICustomerMissedVisitService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(value = "Customer Missed Visit Controller", description = "Manages Customer Missed Visits", produces = "application/json")
@RequestMapping(value = "/api/v1/customer/{customerId}/missedvisit")
@RestController
@Slf4j
public class CustomerMissedVisitController extends BaseCustomerHubController {

    private ICustomerMissedVisitService customerMissedVisitService;

    @Autowired
    public CustomerMissedVisitController(ICustomerMissedVisitService customerMissedVisitService) {
        this.customerMissedVisitService = customerMissedVisitService;
    }

    @ApiOperation(value = "missedVisit, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class)
    })
    @RequestMapping(value = "/{missedVisitCode}/redeem", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateAccountStatus(@PathVariable("customerId") Long customerId,
                                                 @ApiParam("The missed visit code")
                                                 @PathVariable(value = "missedVisitCode") String missedVisitCode
    ) {
        return updateAccountStatus(customerId, missedVisitCode, false);
    }

    @ApiOperation(value = "missedVisit, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class)
    })
    @RequestMapping(value = "/{missedVisitCode}/redeem?validateOnly={validateOnly}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateAccountStatus(@PathVariable("customerId") Long customerId,
                                                 @ApiParam("The missed visit code")
                                                 @PathVariable(value = "missedVisitCode") String missedVisitCode,
                                                 @ApiParam("Validate the missed visit code only")
                                                 @PathVariable(value = "validateOnly") boolean validateOnly
    ) {
        return customerMissedVisitService.redeemMissedVisit(customerId, missedVisitCode, validateOnly)
                .map(response -> returnResponse(response, HttpStatus.NO_CONTENT))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}

// 2288880588988879609816
