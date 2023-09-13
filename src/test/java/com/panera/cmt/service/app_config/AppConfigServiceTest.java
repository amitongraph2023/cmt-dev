package com.panera.cmt.service.app_config;

import com.panera.cmt.entity.AppConfig;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.sort.AppConfigSortColumn;
import com.panera.cmt.repository.IAppConfigRepository;
import com.panera.cmt.test_builders.AppConfigBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.function.Function;

import static com.panera.cmt.test_util.SharedTestUtil.nextEnum;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings("Duplicates")
public class AppConfigServiceTest {

    @Mock
    private IAppConfigRepository appConfigRepository;

    @InjectMocks
    private AppConfigService classUnderTest;



    private Random random = new Random();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createAppConfig_ExistingAppConfigIsFound_ExpectOptionalOfUpdatedAppConfig() {
        AppConfig entity = new AppConfigBuilder().build();

        when(appConfigRepository.save(any(AppConfig.class))).thenReturn(entity);

        Optional<ResponseHolder<AppConfig>> result = classUnderTest.createLocalAppConfig(entity);

        verify(appConfigRepository, times(1)).save(any(AppConfig.class));

        assertTrue(result.isPresent());
        assertEquals(entity.getCode(), result.get().getEntity().getCode());
        assertEquals(entity.getValue(), result.get().getEntity().getValue());
    }
    @Test
    @Ignore // TODO fix this
    public void createAppConfig_SaveIsUnsuccessful_ExpectEmptyOptional() {
        AppConfig entity = new AppConfigBuilder().build();

        when(appConfigRepository.save(any(AppConfig.class))).thenReturn(null);

        Optional<ResponseHolder<AppConfig>> result = classUnderTest.createLocalAppConfig(entity);

        verify(appConfigRepository, times(1)).save(any(AppConfig.class));

        assertFalse(result.isPresent());
    }

    @Test
    public void doesAppConfigExist_WithId_CodeIsFound_ExpectTrue() {
        Long id = random.nextLong();

        when(appConfigRepository.existsById(id)).thenReturn(true);

        boolean result = classUnderTest.doesAppConfigExist(id);

        verify(appConfigRepository, times(1)).existsById(anyLong());

        assertTrue(result);
    }
    @Test
    public void doesAppConfigExist_WithId_CodeIsNotFound_ExpectFalse() {
        Long id = random.nextLong();

        when(appConfigRepository.existsById(id)).thenReturn(false);

        boolean result = classUnderTest.doesAppConfigExist(id);

        verify(appConfigRepository, times(1)).existsById(anyLong());

        assertFalse(result);
    }
    @Test
    @Ignore
    public void doesAppConfigExist_WithCode_CodeIsFound_ExpectTrue() {
        String code = UUID.randomUUID().toString();

        when(appConfigRepository.getByCode(code)).thenReturn(Optional.of(new AppConfig()));

        boolean result = classUnderTest.doesAppConfigExist(code);

        verify(appConfigRepository, times(1)).getByCode(anyString());

        assertTrue(result);
    }
    @Test
    @Ignore // TODO fix this
    public void doesAppConfigExist_WithCode_CodeIsNotFound_ExpectFalse() {
        String code = UUID.randomUUID().toString();

        when(appConfigRepository.getByCode(code)).thenReturn(Optional.empty());

        boolean result = classUnderTest.doesAppConfigExist(code);

        verify(appConfigRepository, times(1)).getByCode(anyString());

        assertFalse(result);
    }

    @Test
    public void doesAppConfigExistExcludeId_CodeIsFound_DifferentId_ExpectTrue() {
        String code = UUID.randomUUID().toString();
        Long id = random.nextLong();
        AppConfig appConfig = new AppConfigBuilder().build();

        when(appConfigRepository.getByCode(code)).thenReturn(Optional.of(appConfig));

        boolean result = classUnderTest.doesAppConfigExistExcludeId(code, id);

        verify(appConfigRepository, times(1)).getByCode(anyString());

        assertTrue(result);
    }
    @Test
    public void doesAppConfigExistExcludeId_CodeIsFound_SameId_ExpectTrue() {
        String code = UUID.randomUUID().toString();
        Long id = random.nextLong();
        AppConfig appConfig = new AppConfigBuilder().withId(id).build();

        when(appConfigRepository.getByCode(code)).thenReturn(Optional.of(appConfig));

        boolean result = classUnderTest.doesAppConfigExistExcludeId(code, id);

        verify(appConfigRepository, times(1)).getByCode(anyString());

        assertFalse(result);
    }
    @Test
    public void doesAppConfigExistExcludeId_CodeIsNotFound_ExpectFalse() {
        String code = UUID.randomUUID().toString();
        Long id = random.nextLong();

        when(appConfigRepository.getByCode(code)).thenReturn(Optional.empty());

        boolean result = classUnderTest.doesAppConfigExistExcludeId(code, id);

        verify(appConfigRepository, times(1)).getByCode(anyString());

        assertFalse(result);
    }

    @Test
    public void deleteAppConfigById_Test() {
        Long id = random.nextLong();

        classUnderTest.deleteLocalAppConfigById(id);

        verify(appConfigRepository).deleteById(id);
    }

    @Test
    public void getAppConfigByCode_ValueIsFound_ExpectOptionalAppConfig() {
        String code = UUID.randomUUID().toString();
        String appConfigCode = UUID.randomUUID().toString();
        AppConfig entity = new AppConfigBuilder().withCode(appConfigCode).build();

        when(appConfigRepository.getByCode(code)).thenReturn(Optional.of(entity));

        Optional<AppConfig> result = classUnderTest.getAppConfigByCode(code);

        verify(appConfigRepository, times(1)).getByCode(anyString());

        assertTrue(result.isPresent());
        assertEquals(entity.getCode(), result.get().getCode());
        assertEquals(entity.getValue(), result.get().getValue());
    }
    @Test
    public void getAppConfigByCode_ValueIsNotFound_ExpectEmptyOptional() {
        String code = UUID.randomUUID().toString();

        when(appConfigRepository.getByCode(code)).thenReturn(Optional.empty());

        Optional<AppConfig> result = classUnderTest.getAppConfigByCode(code);

        verify(appConfigRepository, times(1)).getByCode(anyString());

        assertFalse(result.isPresent());
    }

    @Test
    public void getAppConfigValueByCode_ValueIsFound_ExpectOptionalAppConfig() {
        String code = UUID.randomUUID().toString();
        String appConfigCode = UUID.randomUUID().toString();
        AppConfig entity = new AppConfigBuilder().withCode(appConfigCode).build();

        when(appConfigRepository.getByCode(code)).thenReturn(Optional.of(entity));

        Optional<String> result = classUnderTest.getAppConfigValueByCode(code);

        verify(appConfigRepository, times(1)).getByCode(anyString());

        assertTrue(result.isPresent());
        assertEquals(entity.getValue(), result.get());
    }
    @Test
    public void getAppConfigValueByCode_ValueIsNotFound_ExpectEmptyOptional() {
        String code = UUID.randomUUID().toString();

        when(appConfigRepository.getByCode(code)).thenReturn(Optional.empty());

        Optional<String> result = classUnderTest.getAppConfigValueByCode(code);

        verify(appConfigRepository, times(1)).getByCode(anyString());

        assertFalse(result.isPresent());
    }

    @Test
    public void searchAppConfigByCode_ValueIsFound_ExpectOptionalListOfAppConfig() {
        String code = UUID.randomUUID().toString();
        String appConfigCode = UUID.randomUUID().toString();
        AppConfig entity = new AppConfigBuilder().withCode(appConfigCode).build();

        when(appConfigRepository.searchByCode(code)).thenReturn(singletonList(entity));

        Optional<List<AppConfig>> result = classUnderTest.searchAppConfigByCode(code);

        verify(appConfigRepository, times(1)).searchByCode(anyString());

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(entity.getCode(), result.get().get(0).getCode());
        assertEquals(entity.getValue(), result.get().get(0).getValue());
    }
    @Test
    public void searchAppConfigByCode_NoValuesAreFound_ExpectOptionalEmptyList() {
        String code = UUID.randomUUID().toString();

        when(appConfigRepository.searchByCode(code)).thenReturn(emptyList());

        Optional<List<AppConfig>> result = classUnderTest.searchAppConfigByCode(code);

        verify(appConfigRepository, times(1)).searchByCode(anyString());

        assertTrue(result.isPresent());
        assertEquals(0, result.get().size());
    }

    @Test
    public void searchAppConfigsPaged_AppConfigIsFound_ExpectOptionalOfPageContainingAppConfig() {
        String query = UUID.randomUUID().toString();
        Integer pageNumber = 1;
        Integer size = 1;
        Sort.Direction dir = nextEnum(Sort.Direction.class);
        AppConfigSortColumn col = nextEnum(AppConfigSortColumn.class);
        Page page = mock(Page.class);
        ArgumentCaptor<Pageable> argumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(appConfigRepository.searchAppConfigPaged(eq(query), any(Pageable.class))).thenReturn(page);
        when(page.map(any(Function.class))).thenReturn(page);

        Optional<ResponseHolder<Page<AppConfig>>> result = classUnderTest.searchLocalAppConfigPaged(query, pageNumber, size, dir, col);

        verify(appConfigRepository, times(1)).searchAppConfigPaged(anyString(), argumentCaptor.capture()) ;

        assertTrue(result.isPresent());
        assertEquals((pageNumber - 1), argumentCaptor.getValue().getPageNumber());
        assertEquals((int) size, argumentCaptor.getValue().getPageSize());
    }
    @Test
    public void searchAppConfigsPaged_QueryIsNull_AppConfigIsFound_ExpectOptionalOfPageContainingAppConfig() {
        String query = "";
        Integer pageNumber = 1;
        Integer size = 1;
        Sort.Direction dir = nextEnum(Sort.Direction.class);
        AppConfigSortColumn col = nextEnum(AppConfigSortColumn.class);
        Page page = mock(Page.class);
        ArgumentCaptor<Pageable> argumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(appConfigRepository.searchAppConfigPaged(eq(query), any(Pageable.class))).thenReturn(page);
        when(page.map(any(Function.class))).thenReturn(page);

        Optional<ResponseHolder<Page<AppConfig>>> result = classUnderTest.searchLocalAppConfigPaged(null, pageNumber, size, dir, col);

        verify(appConfigRepository, times(1)).searchAppConfigPaged(anyString(), argumentCaptor.capture()) ;

        assertTrue(result.isPresent());
        assertEquals((pageNumber - 1), argumentCaptor.getValue().getPageNumber());
        assertEquals((int) size, argumentCaptor.getValue().getPageSize());
    }
    @Test
    public void searchAppConfigsPaged_QueryIsALong_AppConfigIsFound_ExpectOptionalOfPageContainingAppConfig() {
        String query = UUID.randomUUID().toString();
        Integer pageNumber = 1;
        Integer size = 1;
        Sort.Direction dir = nextEnum(Sort.Direction.class);
        AppConfigSortColumn col = nextEnum(AppConfigSortColumn.class);
        Page page = mock(Page.class);
        ArgumentCaptor<Pageable> argumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(appConfigRepository.searchAppConfigPaged(eq(query), any(Pageable.class))).thenReturn(page);
        when(page.map(any(Function.class))).thenReturn(page);

        Optional<ResponseHolder<Page<AppConfig>>> result = classUnderTest.searchLocalAppConfigPaged(query, pageNumber, size, dir, col);

        verify(appConfigRepository, times(1)).searchAppConfigPaged(anyString(), argumentCaptor.capture()) ;

        assertTrue(result.isPresent());
        assertEquals((pageNumber - 1), argumentCaptor.getValue().getPageNumber());
        assertEquals((int) size, argumentCaptor.getValue().getPageSize());
    }
    @Test
    @Ignore // TODO fix this
    public void searchAppConfigsPaged_AppConfigIsNotFound_ExpectEmptyOptional() {
        String query = UUID.randomUUID().toString();
        Integer pageNumber = 1;
        Integer size = 1;
        Sort.Direction dir = nextEnum(Sort.Direction.class);
        AppConfigSortColumn col = nextEnum(AppConfigSortColumn.class);
        Page page = mock(Page.class);
        ArgumentCaptor<Pageable> argumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(appConfigRepository.searchAppConfigPaged(eq(query), any(Pageable.class))).thenReturn(null);
        when(page.map(any(Function.class))).thenReturn(page);

        Optional<ResponseHolder<Page<AppConfig>>> result = classUnderTest.searchLocalAppConfigPaged(query, pageNumber, size, dir, col);

        verify(appConfigRepository, times(1)).searchAppConfigPaged(anyString(), argumentCaptor.capture()) ;

        assertFalse(result.isPresent());
        assertEquals((pageNumber - 1), argumentCaptor.getValue().getPageNumber());
        assertEquals((int) size, argumentCaptor.getValue().getPageSize());
    }
    @Test
    public void searchAppConfigsPaged_DirIsNull_ExpectEmptyOfPageContainingAppConfig() {
        String query = UUID.randomUUID().toString();
        Integer pageNumber = 1;
        Integer size = 1;
        AppConfigSortColumn col = nextEnum(AppConfigSortColumn.class);
        Page page = mock(Page.class);
        ArgumentCaptor<Pageable> argumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(appConfigRepository.searchAppConfigPaged(eq(query), any(Pageable.class))).thenReturn(page);
        when(page.map(any(Function.class))).thenReturn(page);

        Optional<ResponseHolder<Page<AppConfig>>> result = classUnderTest.searchLocalAppConfigPaged(query, pageNumber, size, null, col);

        verify(appConfigRepository, times(1)).searchAppConfigPaged(anyString(), argumentCaptor.capture()) ;

        assertTrue(result.isPresent());
        assertEquals((pageNumber - 1), argumentCaptor.getValue().getPageNumber());
        assertEquals((int) size, argumentCaptor.getValue().getPageSize());
    }
    @Test
    public void searchAppConfigsPaged_ColumnIsNull_ExpectEmptyOfPageContainingAppConfig() {
        String query = UUID.randomUUID().toString();
        Integer pageNumber = 1;
        Integer size = 1;
        Sort.Direction dir = nextEnum(Sort.Direction.class);
        Page page = mock(Page.class);
        ArgumentCaptor<Pageable> argumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(appConfigRepository.searchAppConfigPaged(eq(query), any(Pageable.class))).thenReturn(page);
        when(page.map(any(Function.class))).thenReturn(page);

        Optional<ResponseHolder<Page<AppConfig>>> result = classUnderTest.searchLocalAppConfigPaged(query, pageNumber, size, dir, null);

        verify(appConfigRepository, times(1)).searchAppConfigPaged(anyString(), argumentCaptor.capture()) ;

        assertTrue(result.isPresent());
        assertEquals((pageNumber - 1), argumentCaptor.getValue().getPageNumber());
        assertEquals((int) size, argumentCaptor.getValue().getPageSize());
    }

    @Test
    public void updateAppConfig_ExistingAppConfigIsFound_ExpectOptionalOfUpdatedAppConfig() {
        Long id = random.nextLong();
        AppConfig existingEntity = new AppConfigBuilder().withId(id).build();
        AppConfig updatedEntity = new AppConfigBuilder().withId(id).build();

        when(appConfigRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(appConfigRepository.save(any(AppConfig.class))).thenReturn(updatedEntity);

        Optional<ResponseHolder<AppConfig>> result = classUnderTest.updateLocalAppConfig(id, updatedEntity);

        verify(appConfigRepository, times(1)).findById(anyLong());
        verify(appConfigRepository, times(1)).save(any(AppConfig.class));

        assertTrue(result.isPresent());
        assertEquals(updatedEntity.getId(), result.get().getEntity().getId());
        assertEquals(updatedEntity.getCode(), result.get().getEntity().getCode());
        assertEquals(updatedEntity.getValue(), result.get().getEntity().getValue());
    }
    @Test
    public void updateAppConfig_ExistingAppConfigIsNotFound_ExpectEmptyOptional() {
        Long id = random.nextLong();
        AppConfig updatedEntity = new AppConfigBuilder().withId(id).build();

        when(appConfigRepository.findById(id)).thenReturn(Optional.empty());
        when(appConfigRepository.save(any(AppConfig.class))).thenReturn(updatedEntity);

        Optional<ResponseHolder<AppConfig>> result = classUnderTest.updateLocalAppConfig(id, updatedEntity);

        verify(appConfigRepository, times(1)).findById(anyLong());
        verify(appConfigRepository, times(0)).save(any(AppConfig.class));

        assertFalse(result.isPresent());
    }
    @Test
    @Ignore // TODO fix this
    public void updateAppConfig_SaveIsUnsuccessful_ExpectEmptyOptional() {
        Long id = random.nextLong();
        AppConfig existingEntity = new AppConfigBuilder().withId(id).build();
        AppConfig updatedEntity = new AppConfigBuilder().withId(id).build();

        when(appConfigRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(appConfigRepository.save(any(AppConfig.class))).thenReturn(null);

        Optional<ResponseHolder<AppConfig>> result = classUnderTest.updateLocalAppConfig(id, updatedEntity);

        verify(appConfigRepository, times(1)).findById(anyLong());
        verify(appConfigRepository, times(1)).save(any(AppConfig.class));

        assertFalse(result.isPresent());
    }

    @Test
    public void getValuesArrayMap_ValueIsFound_ExpectPopulatedMap() {
        String code = UUID.randomUUID().toString();
        String appConfigCode = UUID.randomUUID().toString();
        AppConfig entity = new AppConfigBuilder().withCode(appConfigCode).build();

        when(appConfigRepository.searchByCode(code)).thenReturn(singletonList(entity));

        Map<String, List<String>> result = classUnderTest.getValuesArrayMap(code);

        verify(appConfigRepository, times(1)).searchByCode(anyString());

        assertEquals(1, result.size());
        assertTrue(result.containsKey(appConfigCode));
        assertEquals(1, result.get(appConfigCode).size());
        assertEquals(entity.getValue(), result.get(appConfigCode).get(0));
    }
    @Test
    public void getValuesArrayMap_NullValueIsFound_ExpectEmptyMap() {
        String code = UUID.randomUUID().toString();

        when(appConfigRepository.searchByCode(code)).thenReturn(singletonList(null));

        Map<String, List<String>> result = classUnderTest.getValuesArrayMap(code);

        verify(appConfigRepository, times(1)).searchByCode(anyString());

        assertEquals(0, result.size());
    }
    @Test
    public void getValuesArrayMap_NoValuesAreFound_ExpectEmptyMap() {
        String code = UUID.randomUUID().toString();

        when(appConfigRepository.searchByCode(code)).thenReturn(emptyList());

        Map<String, List<String>> result = classUnderTest.getValuesArrayMap(code);

        verify(appConfigRepository, times(1)).searchByCode(anyString());

        assertEquals(0, result.size());
    }
}