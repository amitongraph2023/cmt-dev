package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.chub.GeneralPreferenceDTO;
import com.panera.cmt.dto.chub.PersonGeneralPreferenceDTO;
import com.panera.cmt.dto.proxy.chub.Customer;
import com.panera.cmt.dto.proxy.chub.GeneralPreference;
import com.panera.cmt.dto.proxy.chub.PersonGeneralPreference;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.service.chub.ICustomerPreferencesService;
import com.panera.cmt.service.chub.ICustomerService;
import com.panera.cmt.test_builders.*;
import org.junit.Before;
import org.junit.Ignore;
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

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(CustomerPreferencesController.class)
public class CustomerPreferencesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private ICustomerPreferencesService customerPreferencesService;

    @MockBean
    private ICustomerService customerService;

    private Random random = new Random();

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void getUserPreferences_CustomerPreferencesIsFound_Expect200() throws Exception {
        Long id = random.nextLong();
        Customer customer = new CustomerBuilder().build(true);
        GeneralPreference generalPreference = new GeneralPreferenceBuilder().build();

        when(customerService.getCustomer(id)).thenReturn(Optional.of(customer));
        when(customerPreferencesService.getPreferences(id)).thenReturn(Optional.of(generalPreference));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/userpreferences", id)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.foodPreferences", hasSize(1)))
                .andExpect(jsonPath("$.foodPreferences[0].code", is(generalPreference.getFoodPreferences().get(0).getCode())))
                .andExpect(jsonPath("$.foodPreferences[0].displayName", is(generalPreference.getFoodPreferences().get(0).getDisplayName())))
                .andExpect(jsonPath("$.gatherPreference.code", is(generalPreference.getGatherPreference().getCode())))
                .andExpect(jsonPath("$.gatherPreference.displayName", is(generalPreference.getGatherPreference().getDisplayName())));

        verify(customerPreferencesService, times(1)).getPreferences(anyLong());
    }
    
    @Test
    public void updateFoodPreferences_SuccessfulUpdate_Expect204() throws Exception {
        Long id = random.nextLong();
        List<PersonGeneralPreferenceDTO> dto = asList(new PersonGeneralPreferenceDTOBuilder().asFoodPreference().build());
        ResponseHolder<PersonGeneralPreference> responseHolder = new ResponseHolderBuilder<PersonGeneralPreference>().withStatus(HttpStatus.NO_CONTENT).build();

        when(customerPreferencesService.updateFoodPreferences(eq(id), anyList())).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences/dietary", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(customerPreferencesService, times(1)).updateFoodPreferences(anyLong(), anyList());
    }
    @Test
    public void updateFoodPreferences_CustomerIsNotFound_Expect404() throws Exception {
        Long id = random.nextLong();
        List<PersonGeneralPreferenceDTO> dto = asList(new PersonGeneralPreferenceDTOBuilder().asFoodPreference().build());
        ResponseHolder<PersonGeneralPreference> responseHolder = new ResponseHolderBuilder<PersonGeneralPreference>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerPreferencesService.updateFoodPreferences(eq(id), anyList())).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences/dietary", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(customerPreferencesService, times(1)).updateFoodPreferences(anyLong(), anyList());
    }
    @Test
    public void updateFoodPreferences_ValidationError_Expect406() throws Exception {
        Long id = random.nextLong();
        List<PersonGeneralPreferenceDTO> dto = asList(new PersonGeneralPreferenceDTOBuilder().asFoodPreference().build());
        ResponseHolder<PersonGeneralPreference> responseHolder = new ResponseHolderBuilder<PersonGeneralPreference>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerPreferencesService.updateFoodPreferences(eq(id), anyList())).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences/dietary", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is(responseHolder.getErrors().getErrors().get(0).getReasonCode())));

        verify(customerPreferencesService, times(1)).updateFoodPreferences(anyLong(), anyList());
    }
    @Test
    public void updateFoodPreferences_ResponseIsEmpty_Expect500() throws Exception {
        Long id = random.nextLong();
        List<PersonGeneralPreferenceDTO> dto = asList(new PersonGeneralPreferenceDTOBuilder().asFoodPreference().build());

        when(customerPreferencesService.updateFoodPreferences(eq(id), anyList())).thenReturn(Optional.empty());

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences/dietary", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(customerPreferencesService, times(1)).updateFoodPreferences(anyLong(), anyList());
    }

    @Test
    public void updateGatherPreference_SuccessfulUpdate_Expect204() throws Exception {
        Long id = random.nextLong();
        PersonGeneralPreferenceDTO dto = new PersonGeneralPreferenceDTOBuilder().asGatherPreference().build();
        ResponseHolder<PersonGeneralPreference> responseHolder = new ResponseHolderBuilder<PersonGeneralPreference>().withStatus(HttpStatus.NO_CONTENT).build();

        when(customerPreferencesService.updateGatherPreference(eq(id), any(PersonGeneralPreference.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences/gather", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(customerPreferencesService, times(1)).updateGatherPreference(anyLong(), any(PersonGeneralPreference.class));
    }
    @Test
    public void updateGatherPreference_CustomerIsNotFound_Expect404() throws Exception {
        Long id = random.nextLong();
        PersonGeneralPreferenceDTO dto = new PersonGeneralPreferenceDTOBuilder().asGatherPreference().build();
        ResponseHolder<PersonGeneralPreference> responseHolder = new ResponseHolderBuilder<PersonGeneralPreference>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerPreferencesService.updateGatherPreference(eq(id), any(PersonGeneralPreference.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences/gather", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(customerPreferencesService, times(1)).updateGatherPreference(anyLong(), any(PersonGeneralPreference.class));
    }
    @Test
    public void updateGatherPreference_ValidationError_Expect406() throws Exception {
        Long id = random.nextLong();
        PersonGeneralPreferenceDTO dto = new PersonGeneralPreferenceDTOBuilder().asGatherPreference().build();
        ResponseHolder<PersonGeneralPreference> responseHolder = new ResponseHolderBuilder<PersonGeneralPreference>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerPreferencesService.updateGatherPreference(eq(id), any(PersonGeneralPreference.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences/gather", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is(responseHolder.getErrors().getErrors().get(0).getReasonCode())));

        verify(customerPreferencesService, times(1)).updateGatherPreference(anyLong(), any(PersonGeneralPreference.class));
    }
    @Test
    public void updateGatherPreference_ResponseIsEmpty_Expect500() throws Exception {
        Long id = random.nextLong();
        PersonGeneralPreferenceDTO dto = new PersonGeneralPreferenceDTOBuilder().asGatherPreference().build();

        when(customerPreferencesService.updateGatherPreference(eq(id), any(PersonGeneralPreference.class))).thenReturn(Optional.empty());

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences/gather", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(customerPreferencesService, times(1)).updateGatherPreference(anyLong(), any(PersonGeneralPreference.class));
    }

    @Test
    public void updateUserPreferences_SuccessfulUpdate_Expect204() throws Exception {
        Long id = random.nextLong();
        GeneralPreferenceDTO dto = new GeneralPreferenceDTOBuilder().build();
        ResponseHolder<GeneralPreference> responseHolder = new ResponseHolderBuilder<GeneralPreference>().withStatus(HttpStatus.NO_CONTENT).build();

        when(customerPreferencesService.updateUserPreferences(eq(id), any(GeneralPreference.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(customerPreferencesService, times(1)).updateUserPreferences(anyLong(), any(GeneralPreference.class));
    }
    @Test
    public void updateUserPreferences_CustomerIsNotFound_Expect404() throws Exception {
        Long id = random.nextLong();
        GeneralPreferenceDTO dto = new GeneralPreferenceDTOBuilder().build();
        ResponseHolder<GeneralPreference> responseHolder = new ResponseHolderBuilder<GeneralPreference>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerPreferencesService.updateUserPreferences(eq(id), any(GeneralPreference.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(customerPreferencesService, times(1)).updateUserPreferences(anyLong(), any(GeneralPreference.class));
    }
    @Test
    public void updateUserPreferences_ValidationError_Expect406() throws Exception {
        Long id = random.nextLong();
        GeneralPreferenceDTO dto = new GeneralPreferenceDTOBuilder().build();
        ResponseHolder<GeneralPreference> responseHolder = new ResponseHolderBuilder<GeneralPreference>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerPreferencesService.updateUserPreferences(eq(id), any(GeneralPreference.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is(responseHolder.getErrors().getErrors().get(0).getReasonCode())));

        verify(customerPreferencesService, times(1)).updateUserPreferences(anyLong(), any(GeneralPreference.class));
    }
    @Test
    public void updateUserPreferences_ResponseIsEmpty_Expect500() throws Exception {
        Long id = random.nextLong();
        GeneralPreferenceDTO dto = new GeneralPreferenceDTOBuilder().build();

        when(customerPreferencesService.updateUserPreferences(eq(id), any(GeneralPreference.class))).thenReturn(Optional.empty());

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(customerPreferencesService, times(1)).updateUserPreferences(anyLong(), any(GeneralPreference.class));
    }
}