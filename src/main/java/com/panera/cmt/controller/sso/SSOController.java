package com.panera.cmt.controller.sso;

import com.panera.cmt.dto.AuthenticationTokenDTO;
import com.panera.cmt.dto.sso.ImpersonateAuthenticationTokenDTO;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.PermissionType;
import com.panera.cmt.enums.SpoofUnitType;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.service.sso.ISSOService;
import com.panera.cmt.util.SharedUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Api(value = "SSO Controller", description = "Spoofing functionality", produces = "application/json")
@RequestMapping(value = "/api/v1/sso")
@RestController
@Slf4j
public class SSOController extends BaseSSOController {

    private ISSOService ssoService;

    @Value("${sso.cookie-domain}")
    private String cookieDomain;

    @Autowired
    public SSOController(ISSOService ssoService) {
        this.ssoService = ssoService;
    }

    // base functions
    private boolean hasPermission(PermissionType type, AuthenticatedUser authenticatedUser){
        boolean hasPermission = false;
        switch (type) {
            case ADMIN:
                hasPermission = AuthenticatedUserManager.hasAdminRole();
                break;
            case CBSS:
                hasPermission = AuthenticatedUserManager.hasCBSSRole();
                break;
            case SALES_ADMIN:
                hasPermission = AuthenticatedUserManager.hasSalesAdminRole();
                break;
            case COFFEE:
                hasPermission = AuthenticatedUserManager.hasCoffeeRole();
                break;
            case READONLY_CMT:
                hasPermission = AuthenticatedUserManager.hasReadOnlyRole();
                break;
            case SECURITY:
                hasPermission = AuthenticatedUserManager.hasSecurityRole();
        }
        return hasPermission;
    }

    @ApiOperation(value = "getSpoofButtons, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Sales Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Object.class, responseContainer = "List")
    })
    @RequestMapping(value = "/spoof/buttons", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

    @ResponseBody
    public ResponseEntity<?> getSpoofUnits(
            @RequestParam(value = "nonMyPanera", required = false, defaultValue = "false") Boolean nonMyPanera) {
        return ssoService.getSpoofButtons(nonMyPanera)
                .map(data -> new ResponseEntity<>(data, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "postSSOSpoof, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Sales Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = AuthenticationTokenDTO.class),
            @ApiResponse(code = 403, message = "Forbidden", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "/spoof/{customerId}/{unit}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> startCustomerSession(@PathVariable Long customerId, @PathVariable SpoofUnitType unit, HttpServletRequest request, HttpServletResponse response) {
        ImpersonateAuthenticationTokenDTO authenticationTokenDTO;
        Optional<ResponseHolder<ImpersonateAuthenticationTokenDTO>> optionalResponseHolderAuthenticationTokenDTO = this.ssoService.loginCustomerSession(customerId, unit.toString());

        System.out.println(unit.toString());

        if (optionalResponseHolderAuthenticationTokenDTO.isPresent()) {
            authenticationTokenDTO = optionalResponseHolderAuthenticationTokenDTO.get().getEntity();
            if(authenticationTokenDTO != null){
                Cookie cookie = SharedUtils.createCookie("ssoToken", authenticationTokenDTO.getAccessToken(), "/", true, -1, cookieDomain);
                response.addCookie(cookie);

                if (unit != null) {
                    response.addHeader("X-Origin-Source", unit.getXOriginSource());
                }

                return new ResponseEntity<>(authenticationTokenDTO, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @ApiOperation(value = "ssoLogout, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Sales Admin")
    @RequestMapping(value = "/logout/{ssoToken}/{customerId}/{unit}", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseEntity<?> ssoLogout(@PathVariable String ssoToken ,@PathVariable Long customerId, @PathVariable String unit, HttpServletRequest request, HttpServletResponse response) {
        this.ssoService.logoutCustomerSession(ssoToken, customerId, unit);
        if (response.getStatus() == 200) {
            Cookie cookie = SharedUtils.createCookie("ssoToken", ssoToken, "/", true, 0, cookieDomain);
            response.addCookie(cookie);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}