package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.chub.PhoneDTO;
import com.panera.cmt.dto.proxy.chub.Phone;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.service.chub.ICustomerPhoneService;
import com.panera.cmt.test_builders.PhoneBuilder;
import com.panera.cmt.test_builders.PhoneDTOBuilder;
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
@WebMvcTest(CustomerPhoneController.class)
public class CustomerPhoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private ICustomerPhoneService customerPhoneService;

    private Random random = new Random();

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addPhone_SuccessfullyCreated_Expect201() throws Exception {
        Long id = random.nextLong();
        PhoneDTO dto = new PhoneDTOBuilder().build();
        Phone phone = new PhoneBuilder().build(true);
        ResponseHolder<Phone> responseHolder = new ResponseHolderBuilder<Phone>().withEntity(phone).build();

        when(customerPhoneService.createPhone(eq(id), any(Phone.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/phone", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isCreated());

        verify(customerPhoneService, times(1)).createPhone(anyLong(), any(Phone.class));
    }
    @Test
    public void addPhone_CustomerIsNotFound_Expect404() throws Exception {
        Long id = random.nextLong();
        PhoneDTO dto = new PhoneDTOBuilder().build();
        ResponseHolder<Phone> responseHolder = new ResponseHolderBuilder<Phone>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerPhoneService.createPhone(eq(id), any(Phone.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/phone", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(customerPhoneService, times(1)).createPhone(anyLong(), any(Phone.class));
    }
    @Test
    public void addPhone_ValidationError_Expect406() throws Exception {
        Long id = random.nextLong();
        PhoneDTO dto = new PhoneDTOBuilder().build();
        ResponseHolder<Phone> responseHolder = new ResponseHolderBuilder<Phone>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerPhoneService.createPhone(eq(id), any(Phone.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/phone", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable());

        verify(customerPhoneService, times(1)).createPhone(anyLong(), any(Phone.class));
    }
    @Test
    public void addPhone_ResponseIsEmpty_Expect500() throws Exception {
        Long id = random.nextLong();
        PhoneDTO dto = new PhoneDTOBuilder().build();

        when(customerPhoneService.createPhone(eq(id), any(Phone.class))).thenReturn(Optional.empty());

        mockMvc.perform(post(String.format("/api/v1/customer/%d/phone", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(customerPhoneService, times(1)).createPhone(anyLong(), any(Phone.class));
    }

    @Test
    public void deletePhone_SuccessfullyDeleted_Expect204() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        ResponseHolder<Phone> responseHolder = new ResponseHolderBuilder<Phone>().withStatus(HttpStatus.NO_CONTENT).build();

        when(customerPhoneService.deletePhone(anyLong(), anyLong())).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId)))
                .andExpect(status().isNoContent());

        verify(customerPhoneService, times(1)).deletePhone(anyLong(), anyLong());
    }
    @Test
    public void deletePhone_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        ResponseHolder<Phone> responseHolder = new ResponseHolderBuilder<Phone>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerPhoneService.deletePhone(anyLong(), anyLong())).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId)))
                .andExpect(status().isNotFound());

        verify(customerPhoneService, times(1)).deletePhone(anyLong(), anyLong());
    }
    @Test
    public void deletePhone_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        ResponseHolder<Phone> responseHolder = new ResponseHolderBuilder<Phone>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerPhoneService.deletePhone(anyLong(), anyLong())).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId)))
                .andExpect(status().isNotAcceptable());

        verify(customerPhoneService, times(1)).deletePhone(anyLong(), anyLong());
    }
    @Test
    public void deletePhone_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();

        when(customerPhoneService.deletePhone(anyLong(), anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId)))
                .andExpect(status().isInternalServerError());

        verify(customerPhoneService, times(1)).deletePhone(anyLong(), anyLong());
    }

    @Test
    public void getPhone_PhoneIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        Phone phone = new PhoneBuilder().build(true);

        when(customerPhoneService.getPhone(customerId, phoneId)).thenReturn(Optional.of(phone));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", isIntLong(phone.getId())))
                .andExpect(jsonPath("$.phoneNumber", is(phone.getPhoneNumber())))
                .andExpect(jsonPath("$.phoneType", is(phone.getPhoneType())))
                .andExpect(jsonPath("$.countryCode", is(phone.getCountryCode())))
                .andExpect(jsonPath("$.extension", is(phone.getExtension())))
                .andExpect(jsonPath("$.name", is(phone.getName())))
                .andExpect(jsonPath("$.isCallOpt", is(phone.isCallOpt())))
                .andExpect(jsonPath("$.isDefault", is(phone.isDefault())))
                .andExpect(jsonPath("$.isValid", is(phone.isValid())));

        verify(customerPhoneService, times(1)).getPhone(anyLong(), anyLong());
    }
    @Test
    public void getPhone_PhoneIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();

        when(customerPhoneService.getPhone(customerId, phoneId)).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId)))
                .andExpect(status().isNotFound());

        verify(customerPhoneService, times(1)).getPhone(anyLong(), anyLong());
    }

    @Test
    public void getPhones_PhoneIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        Phone phone = new PhoneBuilder().build(true);

        when(customerPhoneService.getPhones(customerId)).thenReturn(Optional.of(singletonList(phone)));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/phone", customerId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", isIntLong(phone.getId())))
                .andExpect(jsonPath("$[0].phoneNumber", is(phone.getPhoneNumber())))
                .andExpect(jsonPath("$[0].phoneType", is(phone.getPhoneType())))
                .andExpect(jsonPath("$[0].countryCode", is(phone.getCountryCode())))
                .andExpect(jsonPath("$[0].extension", is(phone.getExtension())))
                .andExpect(jsonPath("$[0].name", is(phone.getName())))
                .andExpect(jsonPath("$[0].isCallOpt", is(phone.isCallOpt())))
                .andExpect(jsonPath("$[0].isDefault", is(phone.isDefault())))
                .andExpect(jsonPath("$[0].isValid", is(phone.isValid())));

        verify(customerPhoneService, times(1)).getPhones(anyLong());
    }
    @Test
    public void getPhones_PhoneIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();

        when(customerPhoneService.getPhones(customerId)).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/api/v1/customer/%d/phone", customerId)))
                .andExpect(status().isNotFound());

        verify(customerPhoneService, times(1)).getPhones(anyLong());
    }

    @Test
    public void setDefault_SuccessfullySetToDefault_Expect204() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        ResponseHolder<Phone> responseHolder = new ResponseHolderBuilder<Phone>().withStatus(HttpStatus.NO_CONTENT).build();

        when(customerPhoneService.setDefault(anyLong(), anyLong())).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/phone/%d/default", customerId, phoneId)))
                .andExpect(status().isNoContent());

        verify(customerPhoneService, times(1)).setDefault(anyLong(), anyLong());
    }
    @Test
    public void setDefault_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        ResponseHolder<Phone> responseHolder = new ResponseHolderBuilder<Phone>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerPhoneService.setDefault(anyLong(), anyLong())).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/phone/%d/default", customerId, phoneId)))
                .andExpect(status().isNotFound());

        verify(customerPhoneService, times(1)).setDefault(anyLong(), anyLong());
    }
    @Test
    public void setDefault_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        ResponseHolder<Phone> responseHolder = new ResponseHolderBuilder<Phone>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerPhoneService.setDefault(anyLong(), anyLong())).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/phone/%d/default", customerId, phoneId)))
                .andExpect(status().isNotAcceptable());

        verify(customerPhoneService, times(1)).setDefault(anyLong(), anyLong());
    }
    @Test
    public void setDefault_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();

        when(customerPhoneService.setDefault(anyLong(), anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(post(String.format("/api/v1/customer/%d/phone/%d/default", customerId, phoneId)))
                .andExpect(status().isInternalServerError());

        verify(customerPhoneService, times(1)).setDefault(anyLong(), anyLong());
    }

    @Test
    public void updatePhone_SuccessfullyUpdated_Expect204() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        PhoneDTO dto = new PhoneDTOBuilder().build();
        ResponseHolder<Phone> responseHolder = new ResponseHolderBuilder<Phone>().withStatus(HttpStatus.NO_CONTENT).build();

        when(customerPhoneService.updatePhone(eq(customerId), eq(phoneId), any(Phone.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(customerPhoneService, times(1)).updatePhone(anyLong(), anyLong(), any(Phone.class));
    }
    @Test
    public void updatePhone_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        PhoneDTO dto = new PhoneDTOBuilder().build();
        ResponseHolder<Phone> responseHolder = new ResponseHolderBuilder<Phone>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerPhoneService.updatePhone(eq(customerId), eq(phoneId), any(Phone.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(customerPhoneService, times(1)).updatePhone(anyLong(), anyLong(), any(Phone.class));
    }
    @Test
    public void updatePhone_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        PhoneDTO dto = new PhoneDTOBuilder().build();
        ResponseHolder<Phone> responseHolder = new ResponseHolderBuilder<Phone>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerPhoneService.updatePhone(eq(customerId), eq(phoneId), any(Phone.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable());

        verify(customerPhoneService, times(1)).updatePhone(anyLong(), anyLong(), any(Phone.class));
    }
    @Test
    public void updatePhone_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        PhoneDTO dto = new PhoneDTOBuilder().build();

        when(customerPhoneService.updatePhone(eq(customerId), eq(phoneId), any(Phone.class))).thenReturn(Optional.empty());

        mockMvc.perform(put(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(customerPhoneService, times(1)).updatePhone(anyLong(), anyLong(), any(Phone.class));
    }
}