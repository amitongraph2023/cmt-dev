package com.panera.cmt.controller;


import com.panera.cmt.controller.app_config.AppConfigController;
import com.panera.cmt.dto.app_config.AppConfigDTO;
import com.panera.cmt.entity.AppConfig;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.sort.AppConfigSortColumn;
import com.panera.cmt.service.app_config.IAppConfigLocalService;
import com.panera.cmt.test_builders.AppConfigBuilder;
import com.panera.cmt.test_builders.AppConfigDTOBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.test_util.SharedTestUtil.isIntLong;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(AppConfigController.class)
public class AppConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private IAppConfigLocalService appConfigService;

    private Random random = new Random();

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addAppConfig_DataInserted_Expect204() throws Exception {
        AppConfigDTO dto = new AppConfigDTOBuilder().build();
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.doesAppConfigExist(dto.getCode())).thenReturn(false);
        when(appConfigService.createLocalAppConfig(any(AppConfig.class))).thenReturn(Optional.of(new ResponseHolder<>(appConfig)));

        mockMvc.perform(post("/api/v1/app-config")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", isIntLong(appConfig.getId())))
                .andExpect(jsonPath("$.code", is(appConfig.getCode())))
                .andExpect(jsonPath("$.value", is(appConfig.getValue())));

        verify(appConfigService, times(1)).doesAppConfigExist(anyString());
        verify(appConfigService, times(1)).createLocalAppConfig(any(AppConfig.class));
    }
    @Test
    public void addAppConfig_CodeIsNull_Expect406() throws Exception {
        AppConfigDTO dto = new AppConfigDTOBuilder().withCode(null).build();
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.doesAppConfigExist(dto.getCode())).thenReturn(false);
        when(appConfigService.createLocalAppConfig(any(AppConfig.class))).thenReturn(Optional.of(new ResponseHolder<>(appConfig)));

        mockMvc.perform(post("/api/v1/app-config")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is("error.code.required")));

        verify(appConfigService, times(0)).doesAppConfigExist(anyString());
        verify(appConfigService, times(0)).createLocalAppConfig(any(AppConfig.class));
    }
    @Test
    public void addAppConfig_CodeIsTooLong_Expect406() throws Exception {
        AppConfigDTO dto = new AppConfigDTOBuilder().withCode(randomAlphanumeric(51)).build();
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.doesAppConfigExist(dto.getCode())).thenReturn(false);
        when(appConfigService.createLocalAppConfig(any(AppConfig.class))).thenReturn(Optional.of(new ResponseHolder<>(appConfig)));

        mockMvc.perform(post("/api/v1/app-config")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is("error.code.length")));

        verify(appConfigService, times(0)).doesAppConfigExist(anyString());
        verify(appConfigService, times(0)).createLocalAppConfig(any(AppConfig.class));
    }
    @Test
    public void addAppConfig_ValueIsNull_Expect406() throws Exception {
        AppConfigDTO dto = new AppConfigDTOBuilder().withValue(null).build();
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.doesAppConfigExist(dto.getCode())).thenReturn(false);
        when(appConfigService.createLocalAppConfig(any(AppConfig.class))).thenReturn(Optional.of(new ResponseHolder<>(appConfig)));

        mockMvc.perform(post("/api/v1/app-config")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is("error.value.required")));

        verify(appConfigService, times(0)).doesAppConfigExist(anyString());
        verify(appConfigService, times(0)).createLocalAppConfig(any(AppConfig.class));
    }
    @Test
    public void addAppConfig_ValueIsTooLong_Expect406() throws Exception {
        AppConfigDTO dto = new AppConfigDTOBuilder().withValue(randomAlphanumeric(1001)).build();
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.doesAppConfigExist(dto.getCode())).thenReturn(false);
        when(appConfigService.createLocalAppConfig(any(AppConfig.class))).thenReturn(Optional.of(new ResponseHolder<>(appConfig)));

        mockMvc.perform(post("/api/v1/app-config")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is("error.value.length")));

        verify(appConfigService, times(0)).doesAppConfigExist(anyString());
        verify(appConfigService, times(0)).createLocalAppConfig(any(AppConfig.class));
    }
    @Test
    public void addAppConfig_CodeAlreadyExists_Expect406() throws Exception {
        AppConfigDTO dto = new AppConfigDTOBuilder().build();
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.doesAppConfigExist(dto.getCode())).thenReturn(true);
        when(appConfigService.createLocalAppConfig(any(AppConfig.class))).thenReturn(Optional.of(new ResponseHolder<>(appConfig)));

        mockMvc.perform(post("/api/v1/app-config")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is("error.code.duplicate")));

        verify(appConfigService, times(1)).doesAppConfigExist(anyString());
        verify(appConfigService, times(0)).createLocalAppConfig(any(AppConfig.class));
    }
    @Test
    public void addAppConfig_SaveIsUnsuccessful_Expect500() throws Exception {
        AppConfigDTO dto = new AppConfigDTOBuilder().build();

        when(appConfigService.doesAppConfigExist(dto.getCode())).thenReturn(false);
        when(appConfigService.createLocalAppConfig(any(AppConfig.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/app-config")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(appConfigService, times(1)).doesAppConfigExist(anyString());
        verify(appConfigService, times(1)).createLocalAppConfig(any(AppConfig.class));
    }

    @Test
    public void deleteAppConfigById_AppConfigIsFound_Expect204() throws Exception {
        Long id = random.nextLong();

        when(appConfigService.doesAppConfigExist(id)).thenReturn(true);

        mockMvc.perform(delete(String.format("/api/v1/app-config/%d", id)))
                .andExpect(status().isNoContent());

        verify(appConfigService, times(1)).doesAppConfigExist(anyLong());
        verify(appConfigService, times(1)).deleteLocalAppConfigById(id);
    }
    @Test
    public void deleteAppConfigById_AppConfigIsNotFound_Expect404() throws Exception {
        Long id = random.nextLong();

        when(appConfigService.doesAppConfigExist(id)).thenReturn(false);

        mockMvc.perform(delete(String.format("/api/v1/app-config/%d", id)))
                .andExpect(status().isNotFound());

        verify(appConfigService, times(1)).doesAppConfigExist(anyLong());
        verify(appConfigService, times(0)).deleteLocalAppConfigById(id);
    }
    
    @Test
    public void getAllAppConfigsPaged_NoParametersIsSupplied_AppConfigIsFound_Expect200() throws Exception {
        Integer page = 1;
        Integer size = 20;
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.searchLocalAppConfigPaged(null, page, size, null, null)).thenReturn(Optional.of(new ResponseHolder<>(new PageImpl<>(singletonList(appConfig)))));

        mockMvc.perform(get("/api/v1/app-config"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.content[0].id", isIntLong(appConfig.getId())))
                .andExpect(jsonPath("$.content[0].code", is(appConfig.getCode())))
                .andExpect(jsonPath("$.content[0].value", is(appConfig.getValue())))
                .andExpect(jsonPath("$.content[0].lastUpdatedAt", is(appConfig.getLastUpdatedAt())))
                .andExpect(jsonPath("$.content[0].lastUpdatedBy", is(appConfig.getLastUpdatedBy())));

        verify(appConfigService, times(1)).searchLocalAppConfigPaged(or(anyString(), eq(null)), anyInt(), anyInt(), or(any(Sort.Direction.class), eq(null)), or(any(AppConfigSortColumn.class), eq(null)));
    }
    @Test
    public void getAllAppConfigsPaged_QueryParameterIsSupplied_AppConfigIsFound_Expect200() throws Exception {
        String query = UUID.randomUUID().toString();
        Integer page = 1;
        Integer size = 20;
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.searchLocalAppConfigPaged(query, page, size, null, null)).thenReturn(Optional.of(new ResponseHolder<>(new PageImpl<>(singletonList(appConfig)))));

        mockMvc.perform(get(String.format("/api/v1/app-config?query=%s", query)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.content[0].id", isIntLong(appConfig.getId())))
                .andExpect(jsonPath("$.content[0].code", is(appConfig.getCode())))
                .andExpect(jsonPath("$.content[0].value", is(appConfig.getValue())))
                .andExpect(jsonPath("$.content[0].lastUpdatedAt", is(appConfig.getLastUpdatedAt())))
                .andExpect(jsonPath("$.content[0].lastUpdatedBy", is(appConfig.getLastUpdatedBy())));

        verify(appConfigService, times(1)).searchLocalAppConfigPaged(or(anyString(), eq(null)), anyInt(), anyInt(), or(any(Sort.Direction.class), eq(null)), or(any(AppConfigSortColumn.class), eq(null)));
    }
    @Test
    public void getAllAppConfigsPaged_PageParameterIsSupplied_AppConfigIsFound_Expect200() throws Exception {
        Integer page = 5;
        Integer size = 20;
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.searchLocalAppConfigPaged(null, page, size, null, null)).thenReturn(Optional.of(new ResponseHolder<>(new PageImpl<>(singletonList(appConfig)))));

        mockMvc.perform(get(String.format("/api/v1/app-config?page=%d", page)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.content[0].id", isIntLong(appConfig.getId())))
                .andExpect(jsonPath("$.content[0].code", is(appConfig.getCode())))
                .andExpect(jsonPath("$.content[0].value", is(appConfig.getValue())))
                .andExpect(jsonPath("$.content[0].lastUpdatedAt", is(appConfig.getLastUpdatedAt())))
                .andExpect(jsonPath("$.content[0].lastUpdatedBy", is(appConfig.getLastUpdatedBy())))
        ;

        verify(appConfigService, times(1)).searchLocalAppConfigPaged(or(anyString(), eq(null)), anyInt(), anyInt(), or(any(Sort.Direction.class), eq(null)), or(any(AppConfigSortColumn.class), eq(null)));
    }
    @Test
    public void getAllAppConfigsPaged_SizeParameterIsSupplied_AppConfigIsFound_Expect200() throws Exception {
        Integer page = 1;
        Integer size = 5;
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.searchLocalAppConfigPaged(null, page, size, null, null)).thenReturn(Optional.of(new ResponseHolder<>(new PageImpl<>(singletonList(appConfig)))));

        mockMvc.perform(get(String.format("/api/v1/app-config?size=%d", size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.content[0].id", isIntLong(appConfig.getId())))
                .andExpect(jsonPath("$.content[0].code", is(appConfig.getCode())))
                .andExpect(jsonPath("$.content[0].value", is(appConfig.getValue())))
                .andExpect(jsonPath("$.content[0].lastUpdatedAt", is(appConfig.getLastUpdatedAt())))
                .andExpect(jsonPath("$.content[0].lastUpdatedBy", is(appConfig.getLastUpdatedBy())));

        verify(appConfigService, times(1)).searchLocalAppConfigPaged(or(anyString(), eq(null)), anyInt(), anyInt(), or(any(Sort.Direction.class), eq(null)), or(any(AppConfigSortColumn.class), eq(null)));
    }
    @Test
    public void getAllAppConfigsPaged_AllQueryParametersAreSupplied_AppConfigIsFound_Expect200() throws Exception {
        String query = UUID.randomUUID().toString();
        Integer page = 1;
        Integer size = 20;
        AppConfig appConfig = new AppConfigBuilder().build(true);
        Sort.Direction dir = Sort.Direction.ASC;
        AppConfigSortColumn col = AppConfigSortColumn.CODE;

        when(appConfigService.searchLocalAppConfigPaged(query, page, size, dir, col)).thenReturn(Optional.of(new ResponseHolder<>(new PageImpl<>(singletonList(appConfig)))));

        mockMvc.perform(get(String.format("/api/v1/app-config?query=%s&page=%d&size=%d&dir=%s&col=%s", query, page, size, dir, col)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.content[0].id", isIntLong(appConfig.getId())))
                .andExpect(jsonPath("$.content[0].code", is(appConfig.getCode())))
                .andExpect(jsonPath("$.content[0].value", is(appConfig.getValue())))
                .andExpect(jsonPath("$.content[0].lastUpdatedAt", is(appConfig.getLastUpdatedAt())))
                .andExpect(jsonPath("$.content[0].lastUpdatedBy", is(appConfig.getLastUpdatedBy())));

        verify(appConfigService, times(1)).searchLocalAppConfigPaged(or(anyString(), eq(null)), anyInt(), anyInt(), or(any(Sort.Direction.class), eq(null)), or(any(AppConfigSortColumn.class), eq(null)));
    }
    @Test
    public void getAllAppConfigsPaged_AppConfigsAreNotFound_Expect200_ExpectEmptyPage() throws Exception {
        Integer page = 1;
        Integer size = 20;

        when(appConfigService.searchLocalAppConfigPaged(null, page, size, null, null)).thenReturn(Optional.of(new ResponseHolder<>(new PageImpl<>(emptyList()))));

        mockMvc.perform(get("/api/v1/app-config"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.content", hasSize(0)));

        verify(appConfigService, times(1)).searchLocalAppConfigPaged(or(anyString(), eq(null)), anyInt(), anyInt(), or(any(Sort.Direction.class), eq(null)), or(any(AppConfigSortColumn.class), eq(null)));
    }
    @Test
    public void getAllAppConfigsPagedPaged_PageIsZero_Expect406() throws Exception {
        Integer page = 0;
        Integer size = 20;
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.searchLocalAppConfigPaged(null, page, size, null, null)).thenReturn(Optional.of(new ResponseHolder<>(new PageImpl<>(singletonList(appConfig)))));

        mockMvc.perform(get(String.format("/api/v1/app-config?page=%d&size=%d", page, size)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is("error.page.greaterThanOne")));

        verify(appConfigService, times(0)).searchLocalAppConfigPaged(or(anyString(), eq(null)), anyInt(), anyInt(), or(any(Sort.Direction.class), eq(null)), or(any(AppConfigSortColumn.class), eq(null)));
    }
    @Test
    public void getAllAppConfigsPagedPaged_PageIsNegative_Expect406() throws Exception {
        Integer page = -1;
        Integer size = 20;
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.searchLocalAppConfigPaged(null, page, size, null, null)).thenReturn(Optional.of(new ResponseHolder<>(new PageImpl<>(singletonList(appConfig)))));

        mockMvc.perform(get(String.format("/api/v1/app-config?page=%d&size=%d", page, size)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is("error.page.greaterThanOne")));

        verify(appConfigService, times(0)).searchLocalAppConfigPaged(or(anyString(), eq(null)), anyInt(), anyInt(), or(any(Sort.Direction.class), eq(null)), or(any(AppConfigSortColumn.class), eq(null)));
    }
    @Test
    public void getAllAppConfigsPagedPaged_SizeIsZero_Expect406() throws Exception {
        Integer page = 1;
        Integer size = 0;
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.searchLocalAppConfigPaged(null, page, size, null, null)).thenReturn(Optional.of(new ResponseHolder<>(new PageImpl<>(singletonList(appConfig)))));

        mockMvc.perform(get(String.format("/api/v1/app-config?page=%d&size=%d", page, size)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is("error.size.greaterThanOne")));

        verify(appConfigService, times(0)).searchLocalAppConfigPaged(or(anyString(), eq(null)), anyInt(), anyInt(), or(any(Sort.Direction.class), eq(null)), or(any(AppConfigSortColumn.class), eq(null)));
    }
    @Test
    public void getAllAppConfigsPagedPaged_SizeIsNegative_Expect406() throws Exception {
        Integer page = 1;
        Integer size = -1;
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.searchLocalAppConfigPaged(null, page, size, null, null)).thenReturn(Optional.of(new ResponseHolder<>(new PageImpl<>(singletonList(appConfig)))));

        mockMvc.perform(get(String.format("/api/v1/app-config?page=%d&size=%d", page, size)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is("error.size.greaterThanOne")));

        verify(appConfigService, times(0)).searchLocalAppConfigPaged(or(anyString(), eq(null)), anyInt(), anyInt(), or(any(Sort.Direction.class), eq(null)), or(any(AppConfigSortColumn.class), eq(null)));
    }

    @Test
    public void updateAppConfig_DataInserted_Expect204() throws Exception {
        Long id = random.nextLong();
        AppConfigDTO dto = new AppConfigDTOBuilder().build();
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.doesAppConfigExist(id)).thenReturn(true);
        when(appConfigService.doesAppConfigExistExcludeId(dto.getCode(), id)).thenReturn(false);
        when(appConfigService.updateLocalAppConfig(eq(id), any(AppConfig.class))).thenReturn(Optional.of(new ResponseHolder<>(appConfig)));

        mockMvc.perform(put(String.format("/api/v1/app-config/%d", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(appConfigService, times(1)).doesAppConfigExist(anyLong());
        verify(appConfigService, times(1)).doesAppConfigExistExcludeId(anyString(), anyLong());
        verify(appConfigService, times(1)).updateLocalAppConfig(anyLong(), any(AppConfig.class));
    }
    @Test
    public void updateAppConfig_CodeIsNull_Expect406() throws Exception {
        Long id = random.nextLong();
        AppConfigDTO dto = new AppConfigDTOBuilder().withCode(null).build();
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.doesAppConfigExist(id)).thenReturn(true);
        when(appConfigService.doesAppConfigExistExcludeId(dto.getCode(), id)).thenReturn(false);
        when(appConfigService.updateLocalAppConfig(eq(id), any(AppConfig.class))).thenReturn(Optional.of(new ResponseHolder<>(appConfig)));

        mockMvc.perform(put(String.format("/api/v1/app-config/%d", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is("error.code.required")));

        verify(appConfigService, times(0)).doesAppConfigExist(anyLong());
        verify(appConfigService, times(0)).doesAppConfigExistExcludeId(anyString(), anyLong());
        verify(appConfigService, times(0)).updateLocalAppConfig(anyLong(), any(AppConfig.class));
    }
    @Test
    public void updateAppConfig_CodeIsTooLong_Expect406() throws Exception {
        Long id = random.nextLong();
        AppConfigDTO dto = new AppConfigDTOBuilder().withCode(randomAlphanumeric(51)).build();
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.doesAppConfigExist(id)).thenReturn(true);
        when(appConfigService.doesAppConfigExistExcludeId(dto.getCode(), id)).thenReturn(false);
        when(appConfigService.updateLocalAppConfig(eq(id), any(AppConfig.class))).thenReturn(Optional.of(new ResponseHolder<>(appConfig)));

        mockMvc.perform(put(String.format("/api/v1/app-config/%d", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is("error.code.length")));

        verify(appConfigService, times(0)).doesAppConfigExist(anyLong());
        verify(appConfigService, times(0)).doesAppConfigExistExcludeId(anyString(), anyLong());
        verify(appConfigService, times(0)).updateLocalAppConfig(anyLong(), any(AppConfig.class));
    }
    @Test
    public void updateAppConfig_ValueIsNull_Expect406() throws Exception {
        Long id = random.nextLong();
        AppConfigDTO dto = new AppConfigDTOBuilder().withValue(null).build();
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.doesAppConfigExist(id)).thenReturn(true);
        when(appConfigService.doesAppConfigExistExcludeId(dto.getCode(), id)).thenReturn(false);
        when(appConfigService.updateLocalAppConfig(eq(id), any(AppConfig.class))).thenReturn(Optional.of(new ResponseHolder<>(appConfig)));

        mockMvc.perform(put(String.format("/api/v1/app-config/%d", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is("error.value.required")));

        verify(appConfigService, times(0)).doesAppConfigExist(anyLong());
        verify(appConfigService, times(0)).doesAppConfigExistExcludeId(anyString(), anyLong());
        verify(appConfigService, times(0)).updateLocalAppConfig(anyLong(), any(AppConfig.class));
    }
    @Test
    public void updateAppConfig_ValueIsTooLong_Expect406() throws Exception {
        Long id = random.nextLong();
        AppConfigDTO dto = new AppConfigDTOBuilder().withValue(randomAlphanumeric(1001)).build();
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.doesAppConfigExist(id)).thenReturn(true);
        when(appConfigService.doesAppConfigExistExcludeId(dto.getCode(), id)).thenReturn(false);
        when(appConfigService.updateLocalAppConfig(eq(id), any(AppConfig.class))).thenReturn(Optional.of(new ResponseHolder<>(appConfig)));

        mockMvc.perform(put(String.format("/api/v1/app-config/%d", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is("error.value.length")));

        verify(appConfigService, times(0)).doesAppConfigExist(anyLong());
        verify(appConfigService, times(0)).doesAppConfigExistExcludeId(anyString(), anyLong());
        verify(appConfigService, times(0)).updateLocalAppConfig(anyLong(), any(AppConfig.class));
    }
    @Test
    public void updateAppConfig_IdIsNotFound_Expect404() throws Exception {
        Long id = random.nextLong();
        AppConfigDTO dto = new AppConfigDTOBuilder().build();
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.doesAppConfigExist(id)).thenReturn(false);
        when(appConfigService.doesAppConfigExistExcludeId(dto.getCode(), id)).thenReturn(false);
        when(appConfigService.updateLocalAppConfig(eq(id), any(AppConfig.class))).thenReturn(Optional.of(new ResponseHolder<>(appConfig)));

        mockMvc.perform(put(String.format("/api/v1/app-config/%d", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(appConfigService, times(1)).doesAppConfigExist(anyLong());
        verify(appConfigService, times(0)).doesAppConfigExistExcludeId(anyString(), anyLong());
        verify(appConfigService, times(0)).updateLocalAppConfig(anyLong(), any(AppConfig.class));
    }
    @Test
    public void updateAppConfig_CodeAlreadyExists_Expect406() throws Exception {
        Long id = random.nextLong();
        AppConfigDTO dto = new AppConfigDTOBuilder().build();
        AppConfig appConfig = new AppConfigBuilder().build(true);

        when(appConfigService.doesAppConfigExist(id)).thenReturn(true);
        when(appConfigService.doesAppConfigExistExcludeId(dto.getCode(), id)).thenReturn(true);
        when(appConfigService.updateLocalAppConfig(eq(id), any(AppConfig.class))).thenReturn(Optional.of(new ResponseHolder<>(appConfig)));

        mockMvc.perform(put(String.format("/api/v1/app-config/%d", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is("error.code.duplicate")));

        verify(appConfigService, times(1)).doesAppConfigExist(anyLong());
        verify(appConfigService, times(1)).doesAppConfigExistExcludeId(anyString(), anyLong());
        verify(appConfigService, times(0)).updateLocalAppConfig(anyLong(), any(AppConfig.class));
    }
    @Test
    public void updateAppConfig_SaveIsUnsuccessful_Expect500() throws Exception {
        Long id = random.nextLong();
        AppConfigDTO dto = new AppConfigDTOBuilder().build();

        when(appConfigService.doesAppConfigExist(id)).thenReturn(true);
        when(appConfigService.doesAppConfigExistExcludeId(dto.getCode(), id)).thenReturn(false);
        when(appConfigService.updateLocalAppConfig(eq(id), any(AppConfig.class))).thenReturn(Optional.empty());

        mockMvc.perform(put(String.format("/api/v1/app-config/%d", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(appConfigService, times(1)).doesAppConfigExist(anyLong());
        verify(appConfigService, times(1)).doesAppConfigExistExcludeId(anyString(), anyLong());
        verify(appConfigService, times(1)).updateLocalAppConfig(anyLong(), any(AppConfig.class));
    }
}