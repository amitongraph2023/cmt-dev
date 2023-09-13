package com.panera.cmt.service.paytronix;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.panera.cmt.config.Constants;
import com.panera.cmt.config.apigee.paytronix.ApigeePaytronixPropertiesConfig;
import com.panera.cmt.dto.paytronix.BalanceDTO;
import com.panera.cmt.dto.paytronix.TransactionsDTO;
import com.panera.cmt.dto.paytronix.WalletsDTO;
import com.panera.cmt.service.app_config.IAppConfigLocalService;

@Service
public class PaytronixEsbService implements IPaytronixEsbService {

    public static final String TRANSACTION_HISTORY_URI  = "/guest/transactionHistory";  // get  https://developers.paytronix.com/pxs_api_reference/guest.html#get-transaction-history-for-account-by-printed-card-number
    public static final String ADD_REDEEM_URI           = "/transaction/addRedeem";     // post https://developers.paytronix.com/pxs_api_reference/transaction.html#add-or-redeem-value-from-a-card-account
    public static final String GET_BALANCE_URI          = "/guest/accountInformation";  // get  https://developers.paytronix.com/pxs_api_reference/guest.html#get-account-information-balance-inquiry-by-printed-card-number
    public static final String GET_WALLET_CODES_URI     = "/transaction/loadMap";       // post https://developers.paytronix.com/pxs_api_reference/transaction.html#post-transaction-loadMap.json
    public static final String VOID_ADD_REDEEM_URI      = "/transaction/voidAddRedeem"; // post https://developers.paytronix.com/pxs_api_reference/transaction.html#void-an-addition-or-redemption-of-value-from-a-card-account

    public static final String MERCHANT_ID              = "178";
    public static final String STORE_CODE               = "CORP";
    public static final String OPERATOR_ID              = "99999";
    public static final String SENDER_ID                = "PARTNER";
    public static final String PROGRAM_ID               = "PX";
    public static final String POS_TRANSACTION_ID       = "999999";
    public static final int    TERMINAL_ID              = 0;

    private final Logger logger                         = LoggerFactory.getLogger(PaytronixEsbService.class);

    private RestTemplate paytronixApigeeRestTemplate;
    private ApigeePaytronixPropertiesConfig paytronixApigeePropertiesConfig;
    private final IAppConfigLocalService appConfigService;

    @Autowired
    public PaytronixEsbService(@Qualifier("paytronixApigeeRestTemplate") RestTemplate paytronixApigeeRestTemplate, ApigeePaytronixPropertiesConfig paytronixApigeePropertiesConfig,
    		IAppConfigLocalService appConfigService) {
        this.paytronixApigeeRestTemplate = paytronixApigeeRestTemplate;
        this.paytronixApigeePropertiesConfig = paytronixApigeePropertiesConfig;
    
        this.appConfigService = appConfigService;
    }

    @Override
    public Optional<ResponseEntity<String>> addRedeemReward(String loyaltyCard, String checkNumber, BigDecimal checkTotal) {
        logger.info("Executing Paytronix Add/Redeem Reward with card info: {}", loyaltyCard);
        HttpEntity httpEntity = new HttpEntity<>(getAddRedeemRequestBody(loyaltyCard, checkNumber, checkTotal), getHttpHeaders());
        ResponseEntity<String> responseEntity = paytronixApigeeRestTemplate.exchange(paytronixApigeePropertiesConfig.getBaseUrl() + ADD_REDEEM_URI, HttpMethod.POST, httpEntity, String.class);
        logger.info("Completed Paytronix Add/Redeem Reward  card: {}, Status code: {}, responseBody: {}", loyaltyCard, responseEntity.getStatusCode(), responseEntity.getBody());

        return Optional.ofNullable(responseEntity);
    }

    @Override
    public Optional<ResponseEntity<TransactionsDTO>>  getTransactionHistory(String loyaltyCard, String startDate) {
        logger.info("Executing Paytronix get transaction history with card info: {}", loyaltyCard);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(paytronixApigeePropertiesConfig.getBaseUrl() + TRANSACTION_HISTORY_URI)
                .queryParam("printedCardNumber", loyaltyCard)
                .queryParam("merchantId", MERCHANT_ID)
                .queryParam("startDate", startDate);
     HttpEntity httpEntity = new HttpEntity<>(getHttpHeaders());
        ResponseEntity<TransactionsDTO> responseEntity = paytronixApigeeRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, TransactionsDTO.class);
        logger.info("Completed Paytronix get transaction history card: {}, Status code: {}, responseBody: {}", loyaltyCard, responseEntity.getStatusCode(), responseEntity.getBody());
        return Optional.ofNullable(responseEntity);
    }

    @Override
    public Optional<ResponseEntity<WalletsDTO>> getWalletCodes() {
        logger.info("Executing Paytronix get wallet codes");
        HttpEntity httpEntity = new HttpEntity<>(getWalletCodesRequestBody(), getHttpHeaders());
        ResponseEntity<WalletsDTO> responseEntity = paytronixApigeeRestTemplate.exchange(paytronixApigeePropertiesConfig.getBaseUrl() + GET_WALLET_CODES_URI, HttpMethod.POST, httpEntity, WalletsDTO.class);
        logger.info("Completed Paytronix get wallet codes, Status code: {}, responseBody: {}", responseEntity.getStatusCode(), responseEntity.getBody());
        return Optional.ofNullable(responseEntity);
    }

    @Override
    public Optional<ResponseEntity<BalanceDTO>> getBalance(String loyaltyCard) {
        logger.info("Executing Paytronix get balance with card info: {}", loyaltyCard);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(paytronixApigeePropertiesConfig.getBaseUrl() + GET_BALANCE_URI)
                .queryParam("printedCardNumber", loyaltyCard);
        HttpEntity httpEntity = new HttpEntity<>(getHttpHeaders());
        ResponseEntity<BalanceDTO> responseEntity = paytronixApigeeRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, BalanceDTO.class);

        logger.info("Completed Paytronix get balance, card: {}, Status code: {}, responseBody: {}", loyaltyCard, responseEntity.getStatusCode(), responseEntity.getBody());

        return Optional.ofNullable(responseEntity);
    }

    @Override
    public Optional<ResponseEntity<String>> voidAddRedeemReward(String loyaltyCard, String checkNumber, BigDecimal checkTotal) {
        logger.info("Executing Paytronix Void Add/Redeem Reward with card info: {}", loyaltyCard);
        HttpEntity httpEntity = new HttpEntity<>(getAddRedeemRequestBody(loyaltyCard, checkNumber, checkTotal), getHttpHeaders());
        ResponseEntity<String> responseEntity = paytronixApigeeRestTemplate.exchange(paytronixApigeePropertiesConfig.getBaseUrl() + VOID_ADD_REDEEM_URI, HttpMethod.POST, httpEntity, String.class);
        logger.info("Completed Paytronix Void Add/Redeem Reward  card: {}, Status code: {}, responseBody: {}", loyaltyCard, responseEntity.getStatusCode(), responseEntity.getBody());
        return Optional.ofNullable(responseEntity);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add(HttpHeaders.ACCEPT_CHARSET, "utf-8");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer " + appConfigService.getAppConfigValueByCode(Constants.APP_CONFIG_CODE_APIGEE_BEARER_TOKEN).orElse("apigee_token_not_set"));       
        return headers;
    }

    private String getAddRedeemRequestBody(String cardNumber, String checkNumber, BigDecimal checkTotal) {
        return String.format(
                "{\n" +
                        "    \"headerInfo\": {\n" +
                        "        \"merchantId\": " + MERCHANT_ID + ",\n" +
                        "        \"storeCode\": \"" + STORE_CODE + "\",\n" +
                        "        \"operatorId\": \"" + OPERATOR_ID + "\",\n" +
                        "        \"posTransactionId\": \"%s\",\n" +
                        "        \"senderId\": \"" + SENDER_ID + "\",\n" +
                        "        \"programId\": \"" + PROGRAM_ID + "\"\n" +
                        "    },\n" +
                        "    \"cardInfo\": {\n" +
                        "        \"swipeFlag\": false,\n" +
                        "        \"printedCardNumber\": \"%s\"\n" +
                        "    },\n" +
                        "    \"addWalletContents\" : [\n" +
                        "        {\n" +
                        "            \"walletCode\" : 3," +
                        "            \"quantity\" : %s\n" +
                        "        }\n" +
                        "    ], \n" +
                        "    \"redeemWalletContents\" : []\n" +
                        "}\n", checkNumber, cardNumber, checkTotal);
    }

    /**
     * Paytronix Notes from
     * https://developers.paytronix.com/pxs_api_reference/transaction.html#post-transaction-loadMap.json
     * headerInfo (Object) – (required) Header information for the request, as covered above. Send PX as the programId for this request.
     * example:
     * {
     *     "headerInfo": {
     *         "merchantId": 10101010,
     *         "storeCode": "corp",
     *         "operatorId": "1234",
     *         "terminalId": "1023",
     *         "posTransactionId": "999999",
     *         "datetime": "2004-06-01T13:10:01.001",
     *         "posTransactionDatetime": "2004-06-01 13:10",
     *         "senderId": "PARTNER",
     *         "programId": "SV"
     *     }
     * }
     * @return
     */
    private String getWalletCodesRequestBody() {
        return String.format(
                "{\n" +
                        "\"headerInfo\": {\n" +
                        "\"merchantId\": \"%s\",\n" +
                        "\"storeCode\": \"%s\",\n" +
                        "\"operatorId\": \"%s\",\n" +
                        "\"posTransactionId\": \"%s\"\n" +
                        "}\n" +
                        "}",
                MERCHANT_ID,
                STORE_CODE,
                OPERATOR_ID,
                POS_TRANSACTION_ID
        );
    }
}

