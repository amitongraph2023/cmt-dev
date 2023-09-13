package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.chub.SocialIntegrationsDTO;
import com.panera.cmt.service.chub.ISocialIntegrationsService;
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

@Api(value = "Customer Social Integrations Controller", description = "Manages Customer Social Integrations", produces = "application/json")
@RequestMapping(value = "/api/v1/customer/{customerId}/socialintegrations")
@RestController
@Slf4j
public class CustomerSocialIntegrationsController extends BaseCustomerHubController {

    private ISocialIntegrationsService socialIntegrationsService;

    @Autowired
    public CustomerSocialIntegrationsController(ISocialIntegrationsService socialIntegrationsService){
        this.socialIntegrationsService = socialIntegrationsService;
    }

    @ApiOperation(value = "getSocialIntegrations, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SocialIntegrationsDTO.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getSocialIntegrations(@PathVariable("customerId") Long customerId){
        return socialIntegrationsService.getSocialIntegrations(customerId)
                .map(SocialIntegrationsDTO::fromEntity)
                .map(socialIntegrations -> new ResponseEntity<>(socialIntegrations, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
