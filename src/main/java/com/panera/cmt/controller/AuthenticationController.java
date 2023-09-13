package com.panera.cmt.controller;

import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.AuthRequestDTO;
import com.panera.cmt.dto.AuthenticationTokenDTO;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.service.IAuthenticationService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.panera.cmt.config.Constants.SSO_COOKIE_NAME;
import static com.panera.cmt.util.SharedUtils.*;
import static org.springframework.web.util.WebUtils.getCookie;

@Api(value = "Authentication Controller", description = "Manages Authentication", produces = "application/json")
@RequestMapping(value = "/api/v1/authentication")
@RestController
@Slf4j
public class AuthenticationController extends BaseController {

    private IAuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(IAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @ApiOperation(value = "getByAccessToken, Required Authorities: Admin, Cbss, Cbss Supervisor, Coffee, Prod Support, Sales Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = AuthenticationTokenDTO.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = String.class)})
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<AuthenticationTokenDTO> getByAccessToken() {
        return authenticationService.getAuthenticatedUser()
                .map(AuthenticationTokenDTO::fromEntity)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    @ApiOperation(value = "login, Required Authorities: Admin, Cbss, Cbss Supervisor, Coffee, Prod Support, Sales Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = AuthenticationTokenDTO.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class)})
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> login(@ApiParam(value = "Login Request Object")
                                       @Validated @RequestBody() AuthRequestDTO authRequestDTO,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        Optional<AuthenticatedUser> authenticatedUser = authenticationService.login(authRequestDTO.getUsername(), authRequestDTO.getPassword());
        
        if (authenticatedUser.isPresent()) {

            // Set the sso cookie
            response.addCookie(createCookie(SSO_COOKIE_NAME, authenticatedUser.get().getAccessToken(), -1)); // -1 creates a session cookie

            return new ResponseEntity<>(AuthenticationTokenDTO.fromEntity(authenticatedUser.get()), HttpStatus.OK);
        } else {
            return buildAuthValidationErrorResponse();
        }
    }

    @ApiOperation(value = "logout, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Sales Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "OK", response = String.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = String.class)})
    @RequestMapping(value = "", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie ssoCookie = getCookie(request, SSO_COOKIE_NAME);

        if (ssoCookie != null) {
            authenticationService.logout();

            response.addCookie(createCookie(SSO_COOKIE_NAME, ssoCookie.getValue(), 0)); // 0 deletes the cookie
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).contentLength(0L).build();
    }
}
