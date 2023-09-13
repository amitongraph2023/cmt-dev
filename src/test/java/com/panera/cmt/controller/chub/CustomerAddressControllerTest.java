package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.chub.AddressDTO;
import com.panera.cmt.dto.proxy.chub.Address;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.service.chub.ICustomerAddressService;
import com.panera.cmt.test_builders.AddressBuilder;
import com.panera.cmt.test_builders.AddressDTOBuilder;
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
@WebMvcTest(CustomerAddressController.class)
public class CustomerAddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private ICustomerAddressService customerAddressService;

    private Random random = new Random();

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addAddress_SuccessfullyCreated_Expect201() throws Exception {
        Long id = random.nextLong();
        AddressDTO dto = new AddressDTOBuilder().build();
        Address address = new AddressBuilder().build(true);
        ResponseHolder<Address> responseHolder = new ResponseHolderBuilder<Address>().withEntity(address).build();

        when(customerAddressService.createAddress(eq(id), any(Address.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/address", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isCreated());

        verify(customerAddressService, times(1)).createAddress(anyLong(), any(Address.class));
    }
    @Test
    public void addAddress_CustomerIsNotFound_Expect404() throws Exception {
        Long id = random.nextLong();
        AddressDTO dto = new AddressDTOBuilder().build();
        ResponseHolder<Address> responseHolder = new ResponseHolderBuilder<Address>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerAddressService.createAddress(eq(id), any(Address.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/address", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(customerAddressService, times(1)).createAddress(anyLong(), any(Address.class));
    }
    @Test
    public void addAddress_ValidationError_Expect406() throws Exception {
        Long id = random.nextLong();
        AddressDTO dto = new AddressDTOBuilder().build();
        ResponseHolder<Address> responseHolder = new ResponseHolderBuilder<Address>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerAddressService.createAddress(eq(id), any(Address.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/address", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable());

        verify(customerAddressService, times(1)).createAddress(anyLong(), any(Address.class));
    }
    @Test
    public void addAddress_ResponseIsEmpty_Expect500() throws Exception {
        Long id = random.nextLong();
        AddressDTO dto = new AddressDTOBuilder().build();

        when(customerAddressService.createAddress(eq(id), any(Address.class))).thenReturn(Optional.empty());

        mockMvc.perform(post(String.format("/api/v1/customer/%d/address", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(customerAddressService, times(1)).createAddress(anyLong(), any(Address.class));
    }

    @Test
    public void deleteAddress_SuccessfullyDeleted_Expect204() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        ResponseHolder<Address> responseHolder = new ResponseHolderBuilder<Address>().withStatus(HttpStatus.NO_CONTENT).build();

        when(customerAddressService.deleteAddress(anyLong(), anyLong())).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/address/%d", customerId, addressId)))
                .andExpect(status().isNoContent());

        verify(customerAddressService, times(1)).deleteAddress(anyLong(), anyLong());
    }
    @Test
    public void deleteAddress_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        ResponseHolder<Address> responseHolder = new ResponseHolderBuilder<Address>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerAddressService.deleteAddress(anyLong(), anyLong())).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/address/%d", customerId, addressId)))
                .andExpect(status().isNotFound());

        verify(customerAddressService, times(1)).deleteAddress(anyLong(), anyLong());
    }
    @Test
    public void deleteAddress_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        ResponseHolder<Address> responseHolder = new ResponseHolderBuilder<Address>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerAddressService.deleteAddress(anyLong(), anyLong())).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/address/%d", customerId, addressId)))
                .andExpect(status().isNotAcceptable());

        verify(customerAddressService, times(1)).deleteAddress(anyLong(), anyLong());
    }
    @Test
    public void deleteAddress_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();

        when(customerAddressService.deleteAddress(anyLong(), anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/address/%d", customerId, addressId)))
                .andExpect(status().isInternalServerError());

        verify(customerAddressService, times(1)).deleteAddress(anyLong(), anyLong());
    }

    @Test
    public void getAddress_AddressIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        Address address = new AddressBuilder().build(true);

        when(customerAddressService.getAddress(customerId, addressId)).thenReturn(Optional.of(address));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/address/%d", customerId, addressId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", isIntLong(address.getId())))
                .andExpect(jsonPath("$.name", is(address.getName())))
                .andExpect(jsonPath("$.contactPhone", is(address.getContactPhone())))
                .andExpect(jsonPath("$.phoneExtension", is(address.getPhoneExtension())))
                .andExpect(jsonPath("$.additionalInfo", is(address.getAdditionalInfo())))
                .andExpect(jsonPath("$.addressLine1", is(address.getAddressLine1())))
                .andExpect(jsonPath("$.addressLine2", is(address.getAddressLine2())))
                .andExpect(jsonPath("$.city", is(address.getCity())))
                .andExpect(jsonPath("$.state", is(address.getState())))
                .andExpect(jsonPath("$.country", is(address.getCountry())))
                .andExpect(jsonPath("$.zip", is(address.getZip())))
                .andExpect(jsonPath("$.addressType", is(address.getAddressType())))
                .andExpect(jsonPath("$.isDefault", is(address.isDefault())));

        verify(customerAddressService, times(1)).getAddress(anyLong(), anyLong());
    }
    @Test
    public void getAddress_AddressIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();

        when(customerAddressService.getAddress(customerId, addressId)).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/api/v1/customer/%d/address/%d", customerId, addressId)))
                .andExpect(status().isNotFound());

        verify(customerAddressService, times(1)).getAddress(anyLong(), anyLong());
    }

    @Test
    public void getAddresses_AddressIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        Address address = new AddressBuilder().build(true);

        when(customerAddressService.getAddresses(customerId)).thenReturn(Optional.of(singletonList(address)));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/address", customerId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", isIntLong(address.getId())))
                .andExpect(jsonPath("$[0].name", is(address.getName())))
                .andExpect(jsonPath("$[0].contactPhone", is(address.getContactPhone())))
                .andExpect(jsonPath("$[0].phoneExtension", is(address.getPhoneExtension())))
                .andExpect(jsonPath("$[0].additionalInfo", is(address.getAdditionalInfo())))
                .andExpect(jsonPath("$[0].addressLine1", is(address.getAddressLine1())))
                .andExpect(jsonPath("$[0].addressLine2", is(address.getAddressLine2())))
                .andExpect(jsonPath("$[0].city", is(address.getCity())))
                .andExpect(jsonPath("$[0].state", is(address.getState())))
                .andExpect(jsonPath("$[0].country", is(address.getCountry())))
                .andExpect(jsonPath("$[0].zip", is(address.getZip())))
                .andExpect(jsonPath("$[0].addressType", is(address.getAddressType())))
                .andExpect(jsonPath("$[0].isDefault", is(address.isDefault())));

        verify(customerAddressService, times(1)).getAddresses(anyLong());
    }
    @Test
    public void getAddresses_AddressIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();

        when(customerAddressService.getAddresses(customerId)).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/api/v1/customer/%d/address", customerId)))
                .andExpect(status().isNotFound());

        verify(customerAddressService, times(1)).getAddresses(anyLong());
    }

    @Test
    public void updateAddress_SuccessfullyUpdated_Expect204() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        AddressDTO dto = new AddressDTOBuilder().build();
        ResponseHolder<Address> responseHolder = new ResponseHolderBuilder<Address>().withStatus(HttpStatus.NO_CONTENT).build();

        when(customerAddressService.updateAddress(eq(customerId), eq(addressId), any(Address.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/address/%d", customerId, addressId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(customerAddressService, times(1)).updateAddress(anyLong(), anyLong(), any(Address.class));
    }
    @Test
    public void updateAddress_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        AddressDTO dto = new AddressDTOBuilder().build();
        ResponseHolder<Address> responseHolder = new ResponseHolderBuilder<Address>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerAddressService.updateAddress(eq(customerId), eq(addressId), any(Address.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/address/%d", customerId, addressId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(customerAddressService, times(1)).updateAddress(anyLong(), anyLong(), any(Address.class));
    }
    @Test
    public void updateAddress_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        AddressDTO dto = new AddressDTOBuilder().build();
        ResponseHolder<Address> responseHolder = new ResponseHolderBuilder<Address>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerAddressService.updateAddress(eq(customerId), eq(addressId), any(Address.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/address/%d", customerId, addressId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable());

        verify(customerAddressService, times(1)).updateAddress(anyLong(), anyLong(), any(Address.class));
    }
    @Test
    public void updateAddress_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        AddressDTO dto = new AddressDTOBuilder().build();

        when(customerAddressService.updateAddress(eq(customerId), eq(addressId), any(Address.class))).thenReturn(Optional.empty());

        mockMvc.perform(put(String.format("/api/v1/customer/%d/address/%d", customerId, addressId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(customerAddressService, times(1)).updateAddress(anyLong(), anyLong(), any(Address.class));
    }
}