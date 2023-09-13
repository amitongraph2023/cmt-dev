package com.panera.cmt.controller;

import com.panera.cmt.dto.CateringRedirectDTO;
import com.panera.cmt.service.app_config.IAppConfigLocalService;
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

import static com.panera.cmt.config.Constants.APP_CONFIG_CATERING_REDIRECT;

@Api(value = "Catering Redirect Controller", description = "Caterinf Redirect CRUD Operations", produces = "application/json")
@RequestMapping(value = "/api/v1/catering")
@RestController
@Slf4j
public class CateringRedirectController extends BaseController {

    private IAppConfigLocalService appConfigService;

    @Autowired
    public CateringRedirectController(IAppConfigLocalService appConfigService) {
        this.appConfigService = appConfigService;
    }

    @ApiOperation(value = "cateringRedirect, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Sales Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = CateringRedirectDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> addAppConfig() {

        if (
                appConfigService.doesAppConfigExist(APP_CONFIG_CATERING_REDIRECT + ".enabled")
                        && appConfigService.doesAppConfigExist(APP_CONFIG_CATERING_REDIRECT + ".url")
        ) {
            CateringRedirectDTO dto = new CateringRedirectDTO();
            dto.setEnabled(appConfigService.getAppConfigValueByCode(APP_CONFIG_CATERING_REDIRECT + ".enabled").get().equalsIgnoreCase("true"));
            dto.setRedirectUrl(appConfigService.getAppConfigValueByCode(APP_CONFIG_CATERING_REDIRECT + ".url").get());
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
