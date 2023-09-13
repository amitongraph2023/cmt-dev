package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.proxy.chub.SearchCustomer;
import com.panera.cmt.enums.CustomerSearchType;
import com.panera.cmt.service.chub.ICustomerSearchService;
import com.panera.cmt.test_builders.ResponseHolderBuilder;
import com.panera.cmt.test_builders.SearchCustomerBuilder;
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

import java.util.List;
import java.util.UUID;

import static com.panera.cmt.test_util.SharedTestUtil.isIntLong;
import static com.panera.cmt.test_util.SharedTestUtil.nextEnum;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(CustomerSearchController.class)
public class CustomerSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private ICustomerSearchService customerSearchService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void searchCustomers_CustomerIsFound_Expect200WithListOfCustomer() throws Exception {
        CustomerSearchType type = nextEnum(CustomerSearchType.class);
        String value = UUID.randomUUID().toString();
        SearchCustomer searchCustomer = new SearchCustomerBuilder().build(true);

        when(customerSearchService.searchCustomer(type, value)).thenReturn(new ResponseHolderBuilder<List<SearchCustomer>>().withEntity(singletonList(searchCustomer)).build());

        mockMvc.perform(get(String.format("/api/v1/customer/search?type=%s&value=%s", type.name(), value)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].customerId", isIntLong(searchCustomer.getCustomerId())))
                .andExpect(jsonPath("$[0].username", is(searchCustomer.getUsername())))
                .andExpect(jsonPath("$[0].firstName", is(searchCustomer.getFirstName())))
                .andExpect(jsonPath("$[0].lastName", is(searchCustomer.getLastName())))
                .andExpect(jsonPath("$[0].defaultEmail", is(searchCustomer.getDefaultEmail())))
                .andExpect(jsonPath("$[0].defaultPhone", is(searchCustomer.getDefaultPhone())));

        verify(customerSearchService, times(1)).searchCustomer(any(CustomerSearchType.class), anyString());
    }
    @Test
    public void searchCustomers_CustomerIsNotFound_Expect200WithEmptyList() throws Exception {
        CustomerSearchType type = nextEnum(CustomerSearchType.class);
        String value = UUID.randomUUID().toString();

        when(customerSearchService.searchCustomer(type, value)).thenReturn(new ResponseHolderBuilder<List<SearchCustomer>>().withStatus(HttpStatus.NOT_FOUND).build());

        mockMvc.perform(get(String.format("/api/v1/customer/search?type=%s&value=%s", type.name(), value)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(customerSearchService, times(1)).searchCustomer(any(CustomerSearchType.class), anyString());
    }
    @Test
    public void searchCustomers_CustomerIsNotAcceptable_Expect200WithEmptyList() throws Exception {
        CustomerSearchType type = nextEnum(CustomerSearchType.class);
        String value = UUID.randomUUID().toString();

        when(customerSearchService.searchCustomer(type, value)).thenReturn(new ResponseHolderBuilder<List<SearchCustomer>>().withStatus(HttpStatus.NOT_ACCEPTABLE).build());

        this.mockMvc.perform(get(String.format("/api/v1/customer/search?type=%s&value=%s", type.name(), value)))
                .andExpect(status().isNotAcceptable());

        verify(customerSearchService, times(1)).searchCustomer(any(CustomerSearchType.class), anyString());
    }
    @Test
    public void searchCustomers_TypeIsNotIncluded_Expect400() throws Exception {
        CustomerSearchType type = nextEnum(CustomerSearchType.class);
        String value = UUID.randomUUID().toString();
        SearchCustomer searchCustomer = new SearchCustomerBuilder().build(true);

        when(customerSearchService.searchCustomer(type, value)).thenReturn(new ResponseHolderBuilder<List<SearchCustomer>>().withEntity(singletonList(searchCustomer)).build());

        mockMvc.perform(get(String.format("/api/v1/customer/search?value=%s", value)))
                .andExpect(status().isBadRequest());

        verify(customerSearchService, times(0)).searchCustomer(any(CustomerSearchType.class), anyString());
    }
    @Test
    public void searchCustomers_ValueIsNotIncluded_Expect400() throws Exception {
        CustomerSearchType type = nextEnum(CustomerSearchType.class);
        String value = UUID.randomUUID().toString();
        SearchCustomer searchCustomer = new SearchCustomerBuilder().build(true);

        when(customerSearchService.searchCustomer(type, value)).thenReturn(new ResponseHolderBuilder<List<SearchCustomer>>().withEntity(singletonList(searchCustomer)).build());

        mockMvc.perform(get(String.format("/api/v1/customer/search?type=%s", type.name())))
                .andExpect(status().isBadRequest());

        verify(customerSearchService, times(0)).searchCustomer(any(CustomerSearchType.class), anyString());
    }
    @Test
    public void searchCustomers_InvalidType_Expect406() throws Exception {
        CustomerSearchType type = nextEnum(CustomerSearchType.class);
        String value = UUID.randomUUID().toString();
        SearchCustomer searchCustomer = new SearchCustomerBuilder().build(true);

        when(customerSearchService.searchCustomer(type, value)).thenReturn(new ResponseHolderBuilder<List<SearchCustomer>>().withEntity(singletonList(searchCustomer)).build());

        mockMvc.perform(get(String.format("/api/v1/customer/search?type=%s&value=%s", "aaa", value)))
                .andExpect(status().isNotAcceptable());

        verify(customerSearchService, times(0)).searchCustomer(any(CustomerSearchType.class), anyString());
    }
}