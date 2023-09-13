package com.panera.cmt.controller.paytronix;

import com.panera.cmt.dto.paytronix.TransactionsDTO;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.Role;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.mongo.repository.IAuditRepository;
import com.panera.cmt.service.IAuthenticationService;
import com.panera.cmt.service.paytronix.IPaytronixEsbService;
import com.panera.cmt.test_builders.AuthenticatedUserBuilder;
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

import java.util.Optional;
import java.util.Random;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(PaytronixEsbController.class)
public class PaytronixEsbControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private IPaytronixEsbService paytronixService;

    @MockBean
    private IAuthenticationService authenticationService;

    @MockBean
    private IAuditRepository auditRepository;

    private Random random = new Random();

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        AuthenticatedUser authenticatedUser = new AuthenticatedUserBuilder().withRole(Role.ADMIN).build();
        AuthenticatedUserManager.setAuthenticatedUser(authenticatedUser);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @Ignore // todo
    public void getTransactionHistory_CardNumberIsFound_Expect200() throws Exception {
        Long cardNumber = random.nextLong();
        TransactionsDTO paytronixTransactions = new TransactionsDTO();
        ResponseHolder responseHolder = new ResponseHolder<TransactionsDTO>();
        responseHolder.setStatus(HttpStatus.OK);
        responseHolder.setEntity(paytronixTransactions);

//        when(paytronixService.getTransactionHistory(cardNumber.toString(), "2019-09-09")).thenReturn(responseHolder);

        mockMvc.perform(get(String.format("/api/v1/paytronix/%s/transactionHistory", cardNumber.toString())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        verify(paytronixService, times(1)).getTransactionHistory(anyString(), anyString());
    }
}
