package com.panera.cmt.service.paytronix;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.dto.paytronix.TransactionsDTO;
import com.panera.cmt.enums.PaytronixEndpoints;
import com.panera.cmt.mongo.repository.IAuditRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_PAYTRONIX;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static org.junit.Assert.*;

@ActiveProfiles("test")
@SuppressWarnings("Duplicates")
public class PaytronixEsbServiceTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Mock
    private IAuditRepository auditRepository;

    @InjectMocks
    private PaytronixEsbService classUnderTest;


    private String auditSubject = AUDIT_SUBJECT_PAYTRONIX;
    private Random random = new Random();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(classUnderTest, "auth", UUID.randomUUID().toString());
        ReflectionTestUtils.setField(classUnderTest, "baseUrl", "http://localhost:" + wireMockRule.port());
    }


    @Test
    @Ignore // TODo come fix this, was working when returning with response holder
    public void getTransactionHistory_WithCardNumberAndStartDate_Expect200(){
        Long randomLong = random.nextLong();
        String cardNumber = randomLong.toString();
        String startDate = LocalDate.now().minusDays(365).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int statusCode = 200;

        stubFor(get(urlPathMatching(transformEndpoint(PaytronixEndpoints.TRANSACTION_HISTORY)))
                .willReturn(aResponse()
                        .withStatus(statusCode)));

        Optional<ResponseEntity<TransactionsDTO>> result = classUnderTest.getTransactionHistory(cardNumber, startDate);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(PaytronixEndpoints.TRANSACTION_HISTORY))));

        assertTrue(result.isPresent());
    }
}
