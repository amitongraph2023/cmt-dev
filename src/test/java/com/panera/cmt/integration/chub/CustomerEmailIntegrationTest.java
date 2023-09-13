package com.panera.cmt.integration.chub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.Application;
import com.panera.cmt.controller.sso.SSOController;
import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.chub.EmailDTO;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.service.chub.ICustomerEmailService;
import com.panera.cmt.test_builders.AllErrorsDTOBuilder;
import com.panera.cmt.test_builders.EmailDTOBuilder;
import com.panera.cmt.test_util.ChubFactoryUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.test_util.SharedTestUtil.isIntLong;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("integration-test")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class)
@Transactional
public class CustomerEmailIntegrationTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Autowired
    private ICustomerEmailService customerEmailService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    private Random random = new Random();

    @MockBean(name = "paytronixApigeeRestTemplate")
    private RestTemplate paytronixRestTemplate;

    @MockBean
    private SSOController ssoController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        ReflectionTestUtils.setField(customerEmailService, "baseUrl", "http://localhost:" + wireMockRule.port());
    }

    // Adding emails is not currently supported, leaving in until it is
    @Test
    public void addEmail_SuccessfullyCreated_Expect201() throws Exception {
        Long customerId = random.nextLong();
        EmailDTO dto = new EmailDTOBuilder().build();
        Map<String, Object> emailResponse = new ChubFactoryUtil().buildAsEmail();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE)))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(emailResponse))));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/email", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isCreated());

        WireMock.verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE))));
    }
    @Test
    public void addEmail_CustomerIsNotFound_Expect404() throws Exception {
        Long id = random.nextLong();
        EmailDTO dto = new EmailDTOBuilder().build();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/email", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        WireMock.verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE))));
    }
    @Test
    public void addEmail_ValidationError_Expect406() throws Exception {
        Long id = random.nextLong();
        EmailDTO dto = new EmailDTOBuilder().build();
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE)))
                .willReturn(aResponse()
                        .withStatus(406)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/email", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is(errors.getErrors().get(0).getReasonCode())));

        WireMock.verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE))));
    }
    @Test
    public void addEmail_ResponseIsEmpty_Expect500() throws Exception {
        Long id = random.nextLong();
        EmailDTO dto = new EmailDTOBuilder().build();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/email", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        WireMock.verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE))));
    }

    // Deleting emails is not currently supported, leaving in until it is
//    @Test
//    public void deleteEmail_SuccessfullyDeleted_Expect204() throws Exception {
//        Long customerId = random.nextLong();
//        Long emailId = random.nextLong();
//
//        stubFor(WireMock.delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
//                .willReturn(aResponse().withStatus(204)));
//
//        mockMvc.perform(delete(String.format("/api/v1/customer/%d/email/%d", customerId, emailId)))
//                .andExpect(status().isNoContent());
//
//        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));
//    }
//    @Test
//    public void deleteEmail_CustomerIsNotFound_Expect404() throws Exception {
//        Long customerId = random.nextLong();
//        Long emailId = random.nextLong();
//
//        stubFor(WireMock.delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
//                .willReturn(aResponse().withStatus(404)));
//
//        mockMvc.perform(delete(String.format("/api/v1/customer/%d/email/%d", customerId, emailId)))
//                .andExpect(status().isNotFound());
//
//        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));
//    }
//    @Test
//    public void deleteEmail_ValidationError_Expect406() throws Exception {
//        Long customerId = random.nextLong();
//        Long emailId = random.nextLong();
//        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
//
//        stubFor(WireMock.delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
//                .willReturn(aResponse()
//                        .withStatus(406)
//                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                        .withBody(asJsonString(errors))));
//
//        mockMvc.perform(delete(String.format("/api/v1/customer/%d/email/%d", customerId, emailId)))
//                .andExpect(status().isNotAcceptable())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//                .andExpect(jsonPath("$.errors", hasSize(1)))
//                .andExpect(jsonPath("$.errors[0].reasonCode", is(errors.getErrors().get(0).getReasonCode())));
//
//        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));
//    }
//    @Test
//    public void deleteEmail_ResponseIsEmpty_Expect500() throws Exception {
//        Long customerId = random.nextLong();
//        Long emailId = random.nextLong();
//
//        stubFor(WireMock.delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
//                .willReturn(aResponse().withStatus(500)));
//
//        mockMvc.perform(delete(String.format("/api/v1/customer/%d/email/%d", customerId, emailId)))
//                .andExpect(status().isInternalServerError());
//
//        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));
//    }

    @Test
    public void getEmail_EmailIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        Map<String, Object> email = new ChubFactoryUtil().withEmail(randomNumeric(10)).buildAsEmail(true);

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(email))));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/email/%d", customerId, emailId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", isIntLong((Long) email.get("id"))))
                .andExpect(jsonPath("$.emailAddress", is(email.get("emailAddress"))))
                .andExpect(jsonPath("$.emailType", is(email.get("emailType"))))
                .andExpect(jsonPath("$.isDefault", is(email.get("isDefault"))))
                .andExpect(jsonPath("$.isOpt", is(email.get("isOpt"))))
                .andExpect(jsonPath("$.isVerified", is(email.get("isVerified"))));

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));
    }
    @Test
    public void getEmail_EmailIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/email/%d", customerId, emailId)))
                .andExpect(status().isNotFound());

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));
    }

    @Test
    public void getEmails_EmailIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        Map<String, Object> email = new ChubFactoryUtil().withEmail(randomNumeric(10)).buildAsEmail(true);

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(singletonList(email)))));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/email", customerId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", isIntLong((Long) email.get("id"))))
                .andExpect(jsonPath("$[0].emailAddress", is(email.get("emailAddress"))))
                .andExpect(jsonPath("$[0].emailType", is(email.get("emailType"))))
                .andExpect(jsonPath("$[0].isDefault", is(email.get("isDefault"))))
                .andExpect(jsonPath("$[0].isOpt", is(email.get("isOpt"))))
                .andExpect(jsonPath("$[0].isVerified", is(email.get("isVerified"))));

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE))));
    }
    @Test
    public void getEmails_EmailIsNotFound_Expect200WithEmptyList() throws Exception {
        Long customerId = random.nextLong();

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/email", customerId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE))));
    }

    @Test
    public void setDefault_SuccessfullySetToDefault_Expect204() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT)))
                .willReturn(aResponse().withStatus(204)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/email/%d/default", customerId, emailId)))
                .andExpect(status().isNoContent());

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT))));
    }
    @Test
    public void setDefault_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/email/%d/default", customerId, emailId)))
                .andExpect(status().isNotFound());

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT))));
    }
    @Test
    public void setDefault_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT)))
                .willReturn(aResponse()
                        .withStatus(406)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/email/%d/default", customerId, emailId)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is(errors.getErrors().get(0).getReasonCode())));

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT))));
    }
    @Test
    public void setDefault_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/email/%d/default", customerId, emailId)))
                .andExpect(status().isInternalServerError());

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT))));
    }

    @Test
    public void updateEmail_SuccessfullyUpdated_Expect204() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        EmailDTO dto = new EmailDTOBuilder().build();
        Map<String, Object> emailResponse = new ChubFactoryUtil().buildAsEmail();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(emailResponse))));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/email/%d", customerId, emailId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));
    }
    @Test
    public void updateEmail_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        EmailDTO dto = new EmailDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/email/%d", customerId, emailId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));
    }
    @Test
    public void updateEmail_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        EmailDTO dto = new EmailDTOBuilder().build();
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(406)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/email/%d", customerId, emailId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is(errors.getErrors().get(0).getReasonCode())));

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));
    }
    @Test
    public void updateEmail_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        EmailDTO dto = new EmailDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/email/%d", customerId, emailId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));
    }
}
