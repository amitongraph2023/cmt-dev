package com.panera.cmt.test_util;

import com.panera.cmt.dto.proxy.chub.TaxExemption;

import java.util.*;

import static com.panera.cmt.test_util.SharedTestUtil.*;
import static com.panera.cmt.util.SharedUtils.isNull;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.RandomStringUtils.*;

@SuppressWarnings("unchecked")
public class ChubFactoryUtil {

    private Map<String, Object> customerMap;

    public static List<Map<String, Object>> getArray(Map<String, Object> map, String key) {
        if (isNull(map, key)) {
            return null;
        }

        return (List) map.get(key);
    }
    public static Map<String, Object> getObjectFromArray(Map<String, Object> map, String key, int index) {
        if (isNull(map, key)) {
            return null;
        }

        List<Map<String, Object>> array = getArray(map, key);

        return (array != null && array.size() > index) ? array.get(index) : null;
    }

    public ChubFactoryUtil() {
        customerMap = new LinkedHashMap<String, Object>() {{
            put("customerId", new Random().nextLong());
            put("username", UUID.randomUUID().toString());
            put("firstName", UUID.randomUUID().toString());
            put("lastName", UUID.randomUUID().toString());
        }};
    }
    public Map<String, Object> build() {
        return build(false);
    }
    public Map<String, Object> build(boolean convertNumbersToInt) {
        if (convertNumbersToInt) {
            customerMap.put("customerId", nextIntLong());
        }

        return customerMap;
    }

    public Map<String, Object> buildAsAddress() {
        return buildAsAddress(false);
    }
    public Map<String, Object> buildAsAddress(boolean convertNumbersToInt) {
        if (customerMap.get("addresses") == null) {
            withAddress();
        }
        Map<String, Object> addresses = buildAsAddresses().get(0);

        if (convertNumbersToInt) {
            addresses.put("id", nextIntLong());
        }

        return addresses;
    }

    public List<Map<String, Object>> buildAsAddresses() {
        return buildAsAddresses(false);
    }
    public List<Map<String, Object>> buildAsAddresses(boolean convertNumbersToInt) {
        if (customerMap.get("addresses") == null) {
            withAddress();
        }
        return (convertNumbersToInt) ? asList(buildAsAddress(true)) : (List) customerMap.get("addresses");
    }

    public Map<String, Object> buildAsApplePay() {
        return (Map) ((List) buildAsPaymentOptions().get("applePays")).get(0);
    }

    public Map<String, Object> buildAsCreditCard() {
        return (Map) ((List) buildAsPaymentOptions().get("creditCards")).get(0);
    }

    public Map<String, Object> buildAsEmail() {
        return buildAsEmail(false);
    }
    public Map<String, Object> buildAsEmail(boolean convertNumbersToInt) {
        if (customerMap.get("emails") == null) {
            withEmail(randomEmailAddress());
        }
        Map<String, Object> email = buildAsEmails().get(0);

        if (convertNumbersToInt) {
            email.put("id", nextIntLong());
        }

        return email;
    }

    public List<Map<String, Object>> buildAsEmails() {
        return buildAsEmails(false);
    }
    public List<Map<String, Object>> buildAsEmails(boolean convertNumbersToInt) {
        if (customerMap.get("emails") == null) {
            withEmail(randomEmailAddress());
        }
        return (convertNumbersToInt) ? asList(buildAsEmail(true)) : (List) customerMap.get("emails");
    }

    public List<Map<String, Object>> buildAsFoodPreferences() {
        return buildAsFoodPreferences(1);
    }
    public List<Map<String, Object>> buildAsFoodPreferences(int numPreferences) {
        return (List) buildAsUserPreferences(numPreferences).get("foodPreferences");
    }

    public Map<String, Object> buildAsGatherPreference() {
        return (Map) buildAsUserPreferences().get("gatherPreference");
    }

    public Map<String, Object> buildAsGiftCard() {
        return (Map) ((List) buildAsPaymentOptions().get("giftCards")).get(0);
    }

    public Map<String, Object> buildAsPaymentOptions() {
        if (customerMap.get("paymentOptions") == null) {
            withPaymentOptions(1, 1, 1, 1, 1);
        }
        return (Map) customerMap.get("paymentOptions");
    }

    public Map<String, Object> buildAsPayPal() {
        return (Map) ((List) buildAsPaymentOptions().get("payPals")).get(0);
    }

    public Map<String, Object> buildAsPhone() {
        return buildAsPhone(false);
    }
    public Map<String, Object> buildAsPhone(boolean convertNumbersToInt) {
        if (customerMap.get("phones") == null) {
            withPhone(randomNumeric(10));
        }
        Map<String, Object> phone = buildAsPhones().get(0);

        if (convertNumbersToInt) {
            phone.put("id", nextIntLong());
        }

        return phone;
    }

    public List<Map<String, Object>> buildAsPhones() {
        return buildAsPhones(false);
    }
    public List<Map<String, Object>> buildAsPhones(boolean convertNumbersToInt) {
        if (customerMap.get("phones") == null) {
            withPhone(randomNumeric(10));
        }
        return (convertNumbersToInt) ? asList(buildAsPhone(true)) : (List) customerMap.get("phones");
    }

    public Map<String, Object> buildAsUserPreferences() {
        return buildAsUserPreferences(1);
    }
    public Map<String, Object> buildAsUserPreferences(int numFoodPreferences) {
        if (customerMap.get("userPreferences") == null) {
            withUserPreferences(numFoodPreferences);
        }
        return (Map) customerMap.get("userPreferences");
    }

    public Map<String, Object> buildAsSubscriptions() {
        if (customerMap.get("subscriptions") == null) {
            withSubscriptions(1, 1);
        }
        return (Map) customerMap.get("subscriptions");
    }

    public ChubFactoryUtil extended() {
        return extended(true, 1, 1);
    }
    public ChubFactoryUtil extended(int numEmails, int numPhones) {
        return extended(true, numEmails, numPhones);
    }
    public ChubFactoryUtil extended(boolean includeLoyalty, int numEmails, int numPhones) {
        customerMap.put("isSmsGlobalOpt", new Random().nextBoolean());
        customerMap.put("isEmailGlobalOpt", new Random().nextBoolean());
        customerMap.put("isMobilePushOpt", new Random().nextBoolean());
        if (includeLoyalty) {
            customerMap.put("loyalty", new LinkedHashMap<String, Object>() {{
                put("cardNumber", randomNumeric(12));
            }});
        }

        customerMap.put("emails", createEmails(numEmails));
        customerMap.put("phones", createPhones(numPhones));

        return this;
    }

    public ChubFactoryUtil searchResult(String emailAddress, String phoneNumber) {
        customerMap.put("defaultEmail", createEmail(emailAddress, true));
        customerMap.put("defaultPhone", createPhone(phoneNumber, true));
        return this;
    }

    public ChubFactoryUtil withAddress() {
        return withAddress(1);
    }
    public ChubFactoryUtil withAddress(int numAddresses) {
        List<Map<String, Object>> addresses = new LinkedList<>();
        for (int i = 0; i < numAddresses; i++) {
            addresses.add(createAddress(i == 0));
        }
        customerMap.put("addresses", addresses);
        return this;
    }
    public ChubFactoryUtil withBirthday() {
        customerMap.put("birthDate", new LinkedHashMap<String, Object>() {{
            put("birthDay", String.valueOf(randomRange(1, 29)));
            put("birthMonth", String.valueOf(randomRange(1, 12)));
            put("birthYear", "1904");
        }});
        return this;
    }
    public ChubFactoryUtil withEmail(String emailAddress) {
        customerMap.remove("emails");
        customerMap.put("emails", asList(createEmail(emailAddress, true)));
        return this;
    }
    public ChubFactoryUtil withPaymentOptions(int numCreditCards, int numGiftCards, int numPayPals, int numCCAs, int numApplePays) {
        List<Map<String, Object>> applePays = new LinkedList<>();
        for (int i = 0; i < numApplePays; i++) {
            applePays.add(createApplePay());
        }

        List<Map<String, Object>> creditCards = new LinkedList<>();
        for (int i = 0; i < numCreditCards; i++) {
            creditCards.add(createCreditCard(i == 0));
        }

        List<Map<String, Object>> giftCards = new LinkedList<>();
        for (int i = 0; i < numCreditCards; i++) {
            giftCards.add(createGiftCard());
        }

        List<Map<String, Object>> payPals = new LinkedList<>();
        for (int i = 0; i < numCreditCards; i++) {
            payPals.add(createPayPal());
        }

        List<Map<String, Object>> ccas = new LinkedList<>();
        for (int i = 0; i < numCCAs; i++) {
            ccas.add(createCCA());
        }

        customerMap.put("paymentOptions", new LinkedHashMap<String, Object>() {{
            put("applePays", applePays);
            put("creditCards", creditCards);
            put("giftCards", giftCards);
            put("payPals", payPals);
            put("corporateCateringAccounts", ccas);
        }});

        return this;
    }
    public ChubFactoryUtil withPhone(String phoneNumber) {
        customerMap.remove("phones");
        customerMap.put("phones", asList(createPhone(phoneNumber, true)));
        return this;
    }
    public ChubFactoryUtil withSocialIntegration(boolean includeFacebook, boolean includeGoogle) {
        return withSocialIntegration((includeFacebook) ? randomAlphabetic(10) : null, (includeGoogle) ? randomEmailAddress() : null);
    }
    public ChubFactoryUtil withSocialIntegration(String facebookId, String googleId) {
        customerMap.put("socialIntegration", new LinkedHashMap<String, Object>() {{
            put("facebookIntegration", (facebookId != null) ? new LinkedHashMap<String, Object>() {{put("facebookId", facebookId);}} : null);
            put("googleIntegration", (googleId != null) ? new LinkedHashMap<String, Object>() {{put("googleId", googleId);}} : null);
        }});
        return this;
    }
    public ChubFactoryUtil withSubscriptions(int numSubscriptions, int numSuppressors) {
        List<Map<String, Object>> subscriptions = new LinkedList<>();
        for (int i = 0; i < numSubscriptions; i++) {
            subscriptions.add(createSubscription());
        }

        List<Map<String, Object>> suppressors = new LinkedList<>();
        for (int i = 0; i < numSuppressors; i++) {
            suppressors.add(createSuppressor());
        }

        customerMap.put("subscriptions", new LinkedHashMap<String, Object>() {{
            put("subscriptions", subscriptions);
            put("suppressors", suppressors);
        }});

        return this;
    }
    public ChubFactoryUtil withTaxExemption(int numExemptions) {
        List<Map<String, Object>> exemptions = new LinkedList<>();
        for (int i = 0; i < numExemptions; i++) {
            exemptions.add(createTaxExemption());
        }

        customerMap.put("taxExemptions", exemptions);

        return this;
    }
    public ChubFactoryUtil withTaxExemption(TaxExemption existingTaxExemption) {
        customerMap.put("taxExemptions", asList(createTaxExemption(existingTaxExemption)));
        return this;
    }
    public ChubFactoryUtil withUserPreferences(int numFoodPreferences) {
        List<Map<String, Object>> foodPreferences = new LinkedList<>();
        for (int i = 0; i < numFoodPreferences; i++) {
            foodPreferences.add(createUserPreference(1, 6));
        }

        customerMap.put("userPreferences", new LinkedHashMap<String, Object>() {{
            put("foodPreferences", foodPreferences);
            put("gatherPreference", createUserPreference(7, 14));
        }});

        return this;
    }

    private Map<String, Object> createAddress(boolean isDefault) {
        return new LinkedHashMap<String, Object>() {{
            put("id", new Random().nextLong());
            put("name", "panera");
            put("contactPhone", randomNumeric(10));
            put("phoneExtension", randomAlphabetic(10));
            put("additionalInfo", randomAlphabetic(10));
            put("addressLine1", randomAlphanumeric(10));
            put("addressLine2", randomAlphabetic(10));
            put("city", randomAlphabetic(10));
            put("state", "MO");
            put("country", "United States");
            put("zip", randomNumeric(5));
            put("addressType", "Business");
            put("isDefault", isDefault);
            put("addressValidationType", "Address Doctor");
        }};
    }

    private Map<String, Object> createApplePay() {
        return new LinkedHashMap<String, Object>() {{
            put("accountNumber", randomAlphanumeric(20));
        }};
    }

    private Map<String, Object> createCCA() {
        return new LinkedHashMap<String, Object>() {{
            put("orgNumber", new Random().nextLong());
            put("ccaNumber", randomNumeric(10));
            put("clientStartDate", "2018-01-13T12:00:00,000-0600");
            put("clientEndDate", "2099-01-01T00:00:00,000-0600");
            put("orgStartDate", "2018-01-13T07:00:00,000-0600");
            put("orgEndDate", "20299-03-20T00:00:00,000-0500");
            put("ccaBillingName", randomAlphabetic(10));
            put("addressLine1", randomAlphabetic(10));
            put("addressLine2", randomAlphabetic(10));
            put("city", randomAlphabetic(10));
            put("state", "MO");
            put("zipCode", randomNumeric(5));
            put("country", "US");
            put("poRequired", true);
            put("onlineEnabled", true);
        }};
    }

    private Map<String, Object> createCreditCard(boolean isDefault) {
        String token = randomNumeric(12);

        return new LinkedHashMap<String, Object>() {{
            put("token", token);
            put("expirationDate", randomNumeric(4));
            put("cardholderName", randomAlphabetic(10));
            put("lastFour", token.substring(8));
            put("creditCardType", "VISA");
            put("paymentProcessor", randomAlphabetic(10));
            put("paymentLabel", randomAlphabetic(10));
            put("isDefault", isDefault);
        }};
    }

    private Map<String, Object> createEmail(boolean isDefault) {
        return createEmail(null, isDefault);
    }
    private Map<String, Object> createEmail(String emailAddress, boolean isDefault) {
        return new LinkedHashMap<String, Object>() {{
            put("id", new Random().nextLong());
            put("emailAddress", (emailAddress == null) ? randomEmailAddress() : emailAddress);
            put("emailType", "Personal");
            put("isDefault", isDefault);
            put("isOpt", new Random().nextBoolean());
            put("isVerified", new Random().nextBoolean());
        }};
    }

    private List<Map<String, Object>> createEmails(int numEmails) {
        List<Map<String, Object>> emails = new LinkedList<>();
        for (int i = 0; i < numEmails; i++) {
            emails.add(createEmail(i == 0));
        }
        return emails;
    }

    private Map<String, Object> createGiftCard() {
        return new LinkedHashMap<String, Object>() {{
            put("cardNumber", randomNumeric(19));
            put("cardNickname", randomAlphanumeric(10));
        }};
    }

    private Map<String, Object> createPayPal() {
        return new LinkedHashMap<String, Object>() {{
            put("accountNumber", randomAlphanumeric(20));
            put("username", randomEmailAddress());
        }};
    }

    private Map<String, Object> createPhone(boolean isDefault) {
        return createPhone(null, isDefault);
    }
    private Map<String, Object> createPhone(String phoneNumber, boolean isDefault) {
        return new LinkedHashMap<String, Object>() {{
            put("id", new Random().nextLong());
            put("phoneNumber", (phoneNumber == null) ? randomNumeric(10) : phoneNumber);
            put("phoneType", "Personal");
            put("countryCode", "1");
            put("extension", randomAlphabetic(10));
            put("name", randomAlphabetic(10));
            put("isCallOpt", new Random().nextBoolean());
            put("isDefault", isDefault);
            put("isValid", new Random().nextBoolean());
        }};
    }

    private List<Map<String, Object>> createPhones(int numPhones) {
        List<Map<String, Object>> phones = new LinkedList<>();
        for (int i = 0; i < numPhones; i++) {
            phones.add(createPhone(i == 0));
        }
        return phones;
    }

    private Map<String, Object> createSubscription() {
        return createSubscription(null);
    }
    private Map<String, Object> createSubscription(Integer subscriptionCode) {
        return new LinkedHashMap<String, Object>() {{
            put("subscriptionCode", (subscriptionCode != null) ? subscriptionCode : new Random().nextInt());
            put("displayName", randomAlphabetic(10));
            put("isSubscribed", new Random().nextBoolean());
            put("tncVersion", new Random().nextDouble());
        }};
    }

    private Map<String, Object> createSuppressor() {
        return createSuppressor(null);
    }
    private Map<String, Object> createSuppressor(Integer suppressionCode) {
        return new LinkedHashMap<String, Object>() {{
            put("suppressionCode", (suppressionCode != null) ? suppressionCode : new Random().nextInt());
            put("displayName", randomAlphabetic(10));
            put("isSuppressed", new Random().nextBoolean());
        }};
    }

    private Map<String, Object> createTaxExemption() {
        return new LinkedHashMap<String, Object>() {{
            put("id", new Random().nextLong());
            put("company", randomAlphabetic(10));
            put("state", "MO");
            put("country", "United States");
            put("documentUrl", randomAlphabetic(10));
        }};
    }
    private Map<String, Object> createTaxExemption(TaxExemption existingTaxExemption) {
        return new LinkedHashMap<String, Object>() {{
            put("id", new Random().nextLong());
            put("company", existingTaxExemption.getCompany());
            put("state", existingTaxExemption.getState());
            put("country", existingTaxExemption.getCountry());
            put("documentUrl", randomAlphabetic(10));
        }};
    }

    private Map<String, Object> createUserPreference(int min, int max) {
        return new LinkedHashMap<String, Object>() {{
            put("code", randomRange(min, max));
            put("displayName", randomAlphabetic(10));
        }};
    }
}
