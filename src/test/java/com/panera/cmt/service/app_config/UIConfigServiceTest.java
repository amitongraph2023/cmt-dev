package com.panera.cmt.service.app_config;

import com.panera.cmt.entity.AppConfig;
import com.panera.cmt.repository.IAppConfigRepository;
import com.panera.cmt.test_builders.AppConfigBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.panera.cmt.config.Constants.APP_CONFIG_UI_ROUTE_WHITELIST;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings("Duplicates")
public class UIConfigServiceTest {

    @Mock
    private IAppConfigRepository appConfigRepository;

    @InjectMocks
    private UIConfigService classUnderTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getRouteWhiteLists_WhitelistIsFound_ExpectPopulatedMap() {
        String code = UUID.randomUUID().toString();
        AppConfig entity = new AppConfigBuilder().withCode(code).build();

        when(appConfigRepository.searchByCode(APP_CONFIG_UI_ROUTE_WHITELIST)).thenReturn(singletonList(entity));

        Map<String, List<String>> result = classUnderTest.getRouteWhiteLists();

        verify(appConfigRepository, times(1)).searchByCode(anyString());

        assertEquals(1, result.size());
        assertTrue(result.containsKey(code));
        assertEquals(1, result.get(code).size());
        assertEquals(entity.getValue(), result.get(code).get(0));
    }
    @Test
    public void getRouteWhiteLists_NoWhitelistsAreFound_ExpectEmptyMap() {
        when(appConfigRepository.searchByCode(APP_CONFIG_UI_ROUTE_WHITELIST)).thenReturn(emptyList());

        Map<String, List<String>> result = classUnderTest.getRouteWhiteLists();

        verify(appConfigRepository, times(1)).searchByCode(anyString());

        assertEquals(0, result.size());
    }
}