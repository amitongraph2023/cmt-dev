package com.panera.cmt.controller;

import com.panera.cmt.service.app_config.IUIConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(value = "UI Config Controller", description = "UI Config CRUD Operations", produces = "application/json")
@RequestMapping(value = "/api/v1/ui-config")
@RestController
@Slf4j
public class UIConfigController extends BaseController {

    private IUIConfigService uiConfigService;

    @Autowired
    public UIConfigController(IUIConfigService uiConfigService) {
        this.uiConfigService = uiConfigService;
    }

    @ApiOperation(value = "getPermissionWhiteLists, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Read Only, Sales Admin, Security")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Map.class)
    })
    @RequestMapping(value = "/permission-whitelist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getPermissionWhiteLists() {
        return new ResponseEntity<>(uiConfigService.getPermissionWhiteLists(), HttpStatus.OK);
    }
}
