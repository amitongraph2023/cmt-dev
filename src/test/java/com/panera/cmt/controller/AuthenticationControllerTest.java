package com.panera.cmt.controller;

import com.panera.cmt.dto.AuthRequestDTO;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.service.IAuthenticationService;
import com.panera.cmt.test_builders.AuthRequestDTOBuilder;
import com.panera.cmt.test_builders.AuthenticatedUserBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static com.panera.cmt.config.Constants.SSO_COOKIE_NAME;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.test_util.SharedTestUtil.isDateTime;
import static com.panera.cmt.util.SharedUtils.createCookie;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private IAuthenticationService authenticationService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getByAccessToken_AuthenticatedUserIsFound_Expect200() throws Exception {
        AuthenticatedUser authUser = new AuthenticatedUserBuilder().build();

        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(authUser));

        mockMvc.perform(get("/api/v1/authentication"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.username", is(authUser.getUsername())))
                .andExpect(jsonPath("$.firstName", is(authUser.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(authUser.getLastName())))
                .andExpect(jsonPath("$.emailAddress", is(authUser.getEmailAddress())))
                .andExpect(jsonPath("$.accessToken", is(authUser.getAccessToken())))
                .andExpect(jsonPath("$.role", is(authUser.getRole().name())))
                .andExpect(jsonPath("$.loginDate", isDateTime(authUser.getLoginDate())))
                .andExpect(jsonPath("$.expirationDate", isDateTime(authUser.getExpirationDate())));

        verify(authenticationService, times(1)).getAuthenticatedUser();
    }
    @Test
    public void getByAccessToken_AuthenticatedUserIsFound_Expect() throws Exception {
        when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/authentication"))
                .andExpect(status().isUnauthorized());

        verify(authenticationService, times(1)).getAuthenticatedUser();
    }

    @Test
    public void login_AuthenticationIsSuccessful_Expect200() throws Exception {
        AuthRequestDTO dto = new AuthRequestDTOBuilder().build();
        AuthenticatedUser authUser = new AuthenticatedUserBuilder().build();

        when(authenticationService.login(dto.getUsername(), dto.getPassword())).thenReturn(Optional.of(authUser));

        mockMvc.perform(post("/api/v1/authentication")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.username", is(authUser.getUsername())))
                .andExpect(jsonPath("$.firstName", is(authUser.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(authUser.getLastName())))
                .andExpect(jsonPath("$.emailAddress", is(authUser.getEmailAddress())))
                .andExpect(jsonPath("$.accessToken", is(authUser.getAccessToken())))
                .andExpect(jsonPath("$.role", is(authUser.getRole().name())))
                .andExpect(jsonPath("$.loginDate", isDateTime(authUser.getLoginDate())))
                .andExpect(jsonPath("$.expirationDate", isDateTime(authUser.getExpirationDate())));

        verify(authenticationService, times(1)).login(anyString(), anyString());
    }
    @Test
    public void login_UsernameIsNull_Expect406() throws Exception {
        AuthRequestDTO dto = new AuthRequestDTOBuilder().withUsername(null).build();
        AuthenticatedUser authUser = new AuthenticatedUserBuilder().build();

        when(authenticationService.login(dto.getUsername(), dto.getPassword())).thenReturn(Optional.of(authUser));

        mockMvc.perform(post("/api/v1/authentication")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is("error.username.required")));

        verify(authenticationService, times(0)).login(anyString(), anyString());
    }
    @Test
    public void login_PasswordIsNull_Expect406() throws Exception {
        AuthRequestDTO dto = new AuthRequestDTOBuilder().withPassword(null).build();
        AuthenticatedUser authUser = new AuthenticatedUserBuilder().build();

        when(authenticationService.login(dto.getUsername(), dto.getPassword())).thenReturn(Optional.of(authUser));

        mockMvc.perform(post("/api/v1/authentication")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is("error.password.required")));

        verify(authenticationService, times(0)).login(anyString(), anyString());
    }
    @Test
    public void login_AuthenticationIsUnSuccessful_Expect401() throws Exception {
        AuthRequestDTO dto = new AuthRequestDTOBuilder().build();

        when(authenticationService.login(dto.getUsername(), dto.getPassword())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/authentication")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isUnauthorized());

        verify(authenticationService, times(1)).login(anyString(), anyString());
    }

    @Test
    public void logout_CookieExists_Expect204() throws Exception {
        mockMvc.perform(delete("/api/v1/authentication")
                .cookie(createCookie(SSO_COOKIE_NAME, UUID.randomUUID().toString(), -1)))
                .andExpect(status().isNoContent());

        verify(authenticationService, times(1)).logout();
    }
    @Test
    public void logout_CookieDoesNotExist_Expect204() throws Exception {
        mockMvc.perform(delete("/api/v1/authentication"))
                .andExpect(status().isNoContent());

        verify(authenticationService, times(0)).logout();
    }
}