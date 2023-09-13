package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.chub.EmailDTO;
import com.panera.cmt.dto.proxy.chub.Email;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.service.chub.ICustomerEmailService;
import com.panera.cmt.test_builders.EmailBuilder;
import com.panera.cmt.test_builders.EmailDTOBuilder;
import com.panera.cmt.test_builders.ResponseHolderBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.Random;

import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.test_util.SharedTestUtil.isIntLong;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(CustomerEmailController.class)
public class CustomerEmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private ICustomerEmailService customerEmailService;

    private Random random = new Random();

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
    }

    // Adding emails is not currently supported, leaving in until it is
    @Test
    public void addEmail_SuccessfullyCreated_Expect201() throws Exception {
        Long id = random.nextLong();
        EmailDTO dto = new EmailDTOBuilder().build();
        Email email = new EmailBuilder().build(true);
        ResponseHolder<Email> responseHolder = new ResponseHolderBuilder<Email>().withEntity(email).build();

        when(customerEmailService.createEmail(eq(id), any(Email.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/email", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isCreated());

        verify(customerEmailService, times(1)).createEmail(anyLong(), any(Email.class));
    }
    @Test
    public void addEmail_CustomerIsNotFound_Expect404() throws Exception {
        Long id = random.nextLong();
        EmailDTO dto = new EmailDTOBuilder().build();
        ResponseHolder<Email> responseHolder = new ResponseHolderBuilder<Email>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerEmailService.createEmail(eq(id), any(Email.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/email", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(customerEmailService, times(1)).createEmail(anyLong(), any(Email.class));
    }
    @Test
    public void addEmail_ValidationError_Expect406() throws Exception {
        Long id = random.nextLong();
        EmailDTO dto = new EmailDTOBuilder().build();
        ResponseHolder<Email> responseHolder = new ResponseHolderBuilder<Email>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerEmailService.createEmail(eq(id), any(Email.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/email", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable());

        verify(customerEmailService, times(1)).createEmail(anyLong(), any(Email.class));
    }
    @Test
    public void addEmail_ResponseIsEmpty_Expect500() throws Exception {
        Long id = random.nextLong();
        EmailDTO dto = new EmailDTOBuilder().build();

        when(customerEmailService.createEmail(eq(id), any(Email.class))).thenReturn(Optional.empty());

        mockMvc.perform(post(String.format("/api/v1/customer/%d/email", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(customerEmailService, times(1)).createEmail(anyLong(), any(Email.class));
    }

    // Deleting emails is not currently supported, leaving in until it is
//    @Test
//    public void deleteEmail_SuccessfullyDeleted_Expect204() throws Exception {
//        Long customerId = random.nextLong();
//        Long emailId = random.nextLong();
//        ResponseHolder<Email> responseHolder = new ResponseHolderBuilder<Email>().withStatus(HttpStatus.NO_CONTENT).build();
//
//        when(customerEmailService.deleteEmail(anyLong(), anyLong())).thenReturn(Optional.of(responseHolder));
//
//        mockMvc.perform(delete(String.format("/api/v1/customer/%d/email/%d", customerId, emailId)))
//                .andExpect(status().isNoContent());
//
//        verify(customerEmailService, times(1)).deleteEmail(anyLong(), anyLong());
//    }
//    @Test
//    public void deleteEmail_CustomerIsNotFound_Expect404() throws Exception {
//        Long customerId = random.nextLong();
//        Long emailId = random.nextLong();
//        ResponseHolder<Email> responseHolder = new ResponseHolderBuilder<Email>().withStatus(HttpStatus.NOT_FOUND).build();
//
//        when(customerEmailService.deleteEmail(anyLong(), anyLong())).thenReturn(Optional.of(responseHolder));
//
//        mockMvc.perform(delete(String.format("/api/v1/customer/%d/email/%d", customerId, emailId)))
//                .andExpect(status().isNotFound());
//
//        verify(customerEmailService, times(1)).deleteEmail(anyLong(), anyLong());
//    }
//    @Test
//    public void deleteEmail_ValidationError_Expect406() throws Exception {
//        Long customerId = random.nextLong();
//        Long emailId = random.nextLong();
//        ResponseHolder<Email> responseHolder = new ResponseHolderBuilder<Email>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();
//
//        when(customerEmailService.deleteEmail(anyLong(), anyLong())).thenReturn(Optional.of(responseHolder));
//
//        mockMvc.perform(delete(String.format("/api/v1/customer/%d/email/%d", customerId, emailId)))
//                .andExpect(status().isNotAcceptable());
//
//        verify(customerEmailService, times(1)).deleteEmail(anyLong(), anyLong());
//    }
//    @Test
//    public void deleteEmail_ResponseIsEmpty_Expect500() throws Exception {
//        Long customerId = random.nextLong();
//        Long emailId = random.nextLong();
//
//        when(customerEmailService.deleteEmail(anyLong(), anyLong())).thenReturn(Optional.empty());
//
//        mockMvc.perform(delete(String.format("/api/v1/customer/%d/email/%d", customerId, emailId)))
//                .andExpect(status().isInternalServerError());
//
//        verify(customerEmailService, times(1)).deleteEmail(anyLong(), anyLong());
//    }

    @Test
    public void getEmail_EmailIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        Email email = new EmailBuilder().build(true);

        when(customerEmailService.getEmail(customerId, emailId)).thenReturn(Optional.of(email));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/email/%d", customerId, emailId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", isIntLong(email.getId())))
                .andExpect(jsonPath("$.emailAddress", is(email.getEmailAddress())))
                .andExpect(jsonPath("$.emailType", is(email.getEmailType())))
                .andExpect(jsonPath("$.isDefault", is(email.isDefault())))
                .andExpect(jsonPath("$.isOpt", is(email.isOpt())))
                .andExpect(jsonPath("$.isVerified", is(email.isVerified())));

        verify(customerEmailService, times(1)).getEmail(anyLong(), anyLong());
    }
    @Test
    public void getEmail_EmailIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();

        when(customerEmailService.getEmail(customerId, emailId)).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/api/v1/customer/%d/email/%d", customerId, emailId)))
                .andExpect(status().isNotFound());

        verify(customerEmailService, times(1)).getEmail(anyLong(), anyLong());
    }

    @Test
    public void getEmails_EmailIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        Email email = new EmailBuilder().build(true);

        when(customerEmailService.getEmails(customerId)).thenReturn(Optional.of(singletonList(email)));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/email", customerId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", isIntLong(email.getId())))
                .andExpect(jsonPath("$[0].emailAddress", is(email.getEmailAddress())))
                .andExpect(jsonPath("$[0].emailType", is(email.getEmailType())))
                .andExpect(jsonPath("$[0].isDefault", is(email.isDefault())))
                .andExpect(jsonPath("$[0].isOpt", is(email.isOpt())))
                .andExpect(jsonPath("$[0].isVerified", is(email.isVerified())));

        verify(customerEmailService, times(1)).getEmails(anyLong());
    }
    @Test
    public void getEmails_EmailIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();

        when(customerEmailService.getEmails(customerId)).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/api/v1/customer/%d/email", customerId)))
                .andExpect(status().isNotFound());

        verify(customerEmailService, times(1)).getEmails(anyLong());
    }

    @Test
    public void setDefault_SuccessfullySetToDefault_Expect204() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        ResponseHolder<Email> responseHolder = new ResponseHolderBuilder<Email>().withStatus(HttpStatus.NO_CONTENT).build();

        when(customerEmailService.setDefault(anyLong(), anyLong())).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/email/%d/default", customerId, emailId)))
                .andExpect(status().isNoContent());

        verify(customerEmailService, times(1)).setDefault(anyLong(), anyLong());
    }
    @Test
    public void setDefault_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        ResponseHolder<Email> responseHolder = new ResponseHolderBuilder<Email>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerEmailService.setDefault(anyLong(), anyLong())).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/email/%d/default", customerId, emailId)))
                .andExpect(status().isNotFound());

        verify(customerEmailService, times(1)).setDefault(anyLong(), anyLong());
    }
    @Test
    public void setDefault_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        ResponseHolder<Email> responseHolder = new ResponseHolderBuilder<Email>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerEmailService.setDefault(anyLong(), anyLong())).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/email/%d/default", customerId, emailId)))
                .andExpect(status().isNotAcceptable());

        verify(customerEmailService, times(1)).setDefault(anyLong(), anyLong());
    }
    @Test
    public void setDefault_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();

        when(customerEmailService.setDefault(anyLong(), anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(post(String.format("/api/v1/customer/%d/email/%d/default", customerId, emailId)))
                .andExpect(status().isInternalServerError());

        verify(customerEmailService, times(1)).setDefault(anyLong(), anyLong());
    }

    @Test
    public void updateEmail_SuccessfullyUpdated_Expect204() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        EmailDTO dto = new EmailDTOBuilder().build();
        ResponseHolder<Email> responseHolder = new ResponseHolderBuilder<Email>().withStatus(HttpStatus.NO_CONTENT).build();

        when(customerEmailService.updateEmail(eq(customerId), eq(emailId), any(Email.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/email/%d", customerId, emailId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(customerEmailService, times(1)).updateEmail(anyLong(), anyLong(), any(Email.class));
    }
    @Test
    public void updateEmail_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        EmailDTO dto = new EmailDTOBuilder().build();
        ResponseHolder<Email> responseHolder = new ResponseHolderBuilder<Email>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerEmailService.updateEmail(eq(customerId), eq(emailId), any(Email.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/email/%d", customerId, emailId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(customerEmailService, times(1)).updateEmail(anyLong(), anyLong(), any(Email.class));
    }
    @Test
    public void updateEmail_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        EmailDTO dto = new EmailDTOBuilder().build();
        ResponseHolder<Email> responseHolder = new ResponseHolderBuilder<Email>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerEmailService.updateEmail(eq(customerId), eq(emailId), any(Email.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/email/%d", customerId, emailId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable());

        verify(customerEmailService, times(1)).updateEmail(anyLong(), anyLong(), any(Email.class));
    }
    @Test
    public void updateEmail_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        EmailDTO dto = new EmailDTOBuilder().build();

        when(customerEmailService.updateEmail(eq(customerId), eq(emailId), any(Email.class))).thenReturn(Optional.empty());

        mockMvc.perform(put(String.format("/api/v1/customer/%d/email/%d", customerId, emailId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(customerEmailService, times(1)).updateEmail(anyLong(), anyLong(), any(Email.class));
    }
}