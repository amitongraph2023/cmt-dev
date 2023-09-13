package com.panera.cmt.controller.chub;

import com.panera.cmt.service.chub.IStaticDataService;
import org.junit.Before;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(StaticDataController.class)
public class StaticDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private IStaticDataService staticDataService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void searchCustomers_DataIsFound_Expect200() throws Exception {
        String type = UUID.randomUUID().toString();
        String value = UUID.randomUUID().toString();
        Object[] objects = {value};

        when(staticDataService.getStaticData(type)).thenReturn(Optional.of(objects));

        mockMvc.perform(get(String.format("/api/v1/static/chub/%s", type)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", is(value)));

        verify(staticDataService, times(1)).getStaticData(anyString());
    }
    @Test
    public void searchCustomers_DataIsNotFound_Expect404() throws Exception {
        String type = UUID.randomUUID().toString();

        when(staticDataService.getStaticData(type)).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/api/v1/static/chub/%s", type)))
                .andExpect(status().isNotFound());

        verify(staticDataService, times(1)).getStaticData(anyString());
    }
}