package com.panera.cmt.service.chub;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.enums.Role;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.service.app_config.IAppConfigLocalService;
import com.panera.cmt.test_builders.AuthenticatedUserBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static org.junit.Assert.*;

@ActiveProfiles("test")
public class StaticDataServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @InjectMocks
    private StaticDataService classUnderTest;
    
    @Mock
    private IAppConfigLocalService appConfigService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(classUnderTest, "baseUrl", "http://localhost:" + wireMockRule.port());

        AuthenticatedUser authenticatedUser = new AuthenticatedUserBuilder().withRole(Role.ADMIN).build();
        AuthenticatedUserManager.setAuthenticatedUser(authenticatedUser);
    }

    @Test
    public void getStaticData_DataIsFound_ExpectOptionalOfData() {
        String type = UUID.randomUUID().toString();
        String value = UUID.randomUUID().toString();
        Object[] objects = {value};

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.STATIC_DATA_BY_TYPE)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(objects))));

        Optional<Object[]> result = classUnderTest.getStaticData(type);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.STATIC_DATA_BY_TYPE))));

        assertTrue(result.isPresent());
        assertEquals(1, result.get().length);
        assertEquals(value, result.get()[0]);
    }
    @Test
    public void getStaticData_DataIsNotFound_ExpectEmptyOptional() {
        String type = UUID.randomUUID().toString();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.STATIC_DATA_BY_TYPE)))
                .willReturn(aResponse().withStatus(404)));

        Optional<Object[]> result = classUnderTest.getStaticData(type);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.STATIC_DATA_BY_TYPE))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getStaticData_ResponseIs500_ExpectEmptyOptional() {
        String type = UUID.randomUUID().toString();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.STATIC_DATA_BY_TYPE)))
                .willReturn(aResponse().withStatus(500)));

        Optional<Object[]> result = classUnderTest.getStaticData(type);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.STATIC_DATA_BY_TYPE))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getStaticData_TypeIsNull_ExpectEmptyOptional() {
        String value = UUID.randomUUID().toString();
        Object[] objects = {value};

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.STATIC_DATA_BY_TYPE)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(objects))));

        Optional<Object[]> result = classUnderTest.getStaticData(null);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.STATIC_DATA_BY_TYPE))));

        assertFalse(result.isPresent());
    }
}