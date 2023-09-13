package com.panera.cmt.controller.lmt;

import com.panera.cmt.enums.PermissionType;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.service.IAuthenticationService;
import com.panera.cmt.service.lmt.ILMTService;
import com.panera.cmt.service.lmt.LMTService;
import com.panera.cmt.service.sso.ISSOService;
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

@Api(value = "LMT Controller", description = "LTO Lookup", produces = "application/json")
@RequestMapping(value = "/api/v1/lmt")
@RestController
@Slf4j
public class LMTController extends BaseLMTController {

    private IAuthenticationService authenticationService;
    private ILMTService lmtService;

    @Autowired
    public LMTController(IAuthenticationService authenticationService, LMTService lmtService) {
        this.authenticationService = authenticationService;
        this.lmtService = lmtService;
    }

    @ApiOperation(value = "getLTOByCode")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Object.class),
            @ApiResponse(code = 404, message = "Not found", response = String.class)
    })
    @RequestMapping(value = "LTO/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getLTOByCode(@PathVariable("code") String specialCode) {
        return lmtService.getLTObyCode(specialCode.toUpperCase())
                .map(data -> {
                    return new ResponseEntity<>(data, HttpStatus.OK);
                }
                )
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
