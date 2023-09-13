package com.panera.cmt.util;

import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.enums.PaymentOptionType;
import com.panera.cmt.enums.PaytronixEndpoints;
import com.panera.cmt.enums.SSOEndpoints;
import org.apache.catalina.util.URLEncoder;

import javax.servlet.http.Cookie;
import java.util.UUID;

import static org.springframework.util.StringUtils.isEmpty;

public class SharedUtils {

    private final static int maxGUIDWithDashes = 36,
                             maxGUIDWithoutDashes = 32;

    /**
     * Creates a cookie
     *
     * @see javax.servlet.http
     * @param name Name of the cookie
     * @param value Value of the cookie
     * @param maxAge Expiration date of the cookie
     * @return A cookie with path defaulted to '/' and is secure
     */
    public static Cookie createCookie(String name, String value, int maxAge) {
        return createCookie(name, value, "/", true, maxAge);
    }
    /**
     * Creates a cookie
     *
     * @see javax.servlet.http
     * @param name Name of the cookie
     * @param value Value of the cookie
     * @param isSecure Is the cookie https only
     * @param maxAge Expiration date of the cookie
     * @return A cookie with path defaulted to '/' and is secure
     */
    public static Cookie createCookie(String name, String value, boolean isSecure, int maxAge) {
        return createCookie(name, value, "/", isSecure, maxAge);
    }

    /**
     * Creates a cookie
     *
     * @see javax.servlet.http
     * @param name Name of the cookie
     * @param value Value of the cookie
     * @param path The path of the cookie
     * @param isSecure Is the cookie https only
     * @param maxAge Expiration date of the cookie
     * @return A cookie with path defaulted to '/' and is secure
     */
    public static Cookie createCookie(String name, String value, String path, boolean isSecure, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
//        cookie.setSecure(isSecure);
        cookie.setMaxAge(maxAge);

        return cookie;
    }
    /**
     * Creates a cookie
     *
     * @see javax.servlet.http
     * @param name Name of the cookie
     * @param value Value of the cookie
     * @param path The path of the cookie
     * @param isSecure Is the cookie https only
     * @param maxAge Expiration date of the cookie
     * @param domain Domain for the cookie
     * @return A cookie with path defaulted to '/' and is secure
     */
    public static Cookie createCookie(String name, String value, String path, boolean isSecure, int maxAge, String domain) {
        Cookie cookie = createCookie(name, value, path, isSecure, maxAge);
        cookie.setDomain(domain);
        cookie.setSecure(isSecure);
        return cookie;
    }


    /**
     * Creates a urlEncoded cookie
     *
     * @see javax.servlet.http
     * @param name Name of the cookie
     * @param value Value of the cookie
     * @param maxAge Expiration date of the cookie
     * @return A cookie with path defaulted to '/' and is secure
     */
    public static Cookie createUrlEncodedCookie(String name, String value, String path, boolean isSecure, int maxAge, String domain) {
        URLEncoder urlEncoder = new URLEncoder();
        String encodedValue = urlEncoder.encode(value, "UTF-8");
        return createCookie(name, encodedValue, path, isSecure, maxAge, domain);
    }
    /**
     * Creates a GUID
     *
     * @return A 36 character GUID
     */
    public static String createGUID() {
        return createGUID(maxGUIDWithDashes, true);
    }
    /**
     * Creates a GUID
     *
     * @param length The length of the GUID (1-maxGUIDWithDashes)
     * @return A n character GUID
     * @throws IllegalArgumentException Exception if the length is out of bounds
     */
    public static String createGUID(int length) throws IllegalArgumentException {
        return createGUID(length, true);
    }
    /**
     * Creates a GUID
     *
     * @param includeDashes Include dashes
     * @return A 36 character GUID with/without dashes
     */
    public static String createGUID(boolean includeDashes) {
        return createGUID((includeDashes) ? maxGUIDWithDashes : maxGUIDWithoutDashes, includeDashes);
    }
    /**
     * Creates a GUID
     *
     * @param length The length of the GUID (1-maxGUIDWithDashes)
     * @return A n character GUID with/without dashes
     * @throws IllegalArgumentException Exception if the length is out of bounds
     */
    public static String createGUID(int length, boolean includeDashes) throws IllegalArgumentException {
        if (length < 1) {
            throw new IllegalArgumentException("length must be at least 1");
        }

        if (!includeDashes && length > maxGUIDWithoutDashes) {
            throw new IllegalArgumentException("length must be no more than " + String.valueOf(maxGUIDWithoutDashes));
        } else if (includeDashes && length > maxGUIDWithDashes) {
            throw new IllegalArgumentException("length must be no more than " + String.valueOf(maxGUIDWithDashes));
        }

        String guid = UUID.randomUUID().toString().toUpperCase();

        if (!includeDashes) {
            guid = guid.replace("-", "");
        }

        return guid.substring(0, length - 1);
    }

    /**
     * **********************************************
     * Checks if any of the provided objects are null
     *
     * @param objects Array of objects
     * @return The null status of all objects
     */
    public static boolean isAnyNull(Object... objects) {
        if (objects == null) {
            return true;
        }

        for (Object object : objects) {
            if (object == null
                    || (object instanceof String && isEmpty(object)) // Check if string is empty
                    ) {
                return true;
            }
        }

        return false;
    }

    /**
     * **********************************************
     * Checks if any of the provided objects are null
     *
     * @param objects Array of objects
     * @return The null status of all objects
     */
    public static boolean isNull(Object... objects) {
        if (objects == null) {
            return true;
        }

        for (Object object : objects) {
            if (object == null
                    || (object instanceof String && isEmpty(object)) // Check if string is empty
                    ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Replaces all params in the endpoint to '(.*)' for wiremock matching
     *
     * @param endpoint The endpoint to transform
     * @return The transformed string
     */
    public static String transformEndpoint(ChubEndpoints endpoint) {
        return endpoint.getStub().replaceAll("\\{.*?\\}", "(.*)");
    }
    public static String transformEndpoint(PaytronixEndpoints endpoint) {
        return endpoint.getStub().replaceAll("\\{.*?\\}", "(.*)");
    }
    public static String transformEndpoint(SSOEndpoints endpoint) {
        return endpoint.getStub().replaceAll("\\{.*?\\}", "(.*)");
    }

    /**
     * Replaces all params in the endpoint to '(.*)' for wiremock matching
     *
     * @param endpoint The endpoint to transform
     * @param type The payment option type
     * @return The transformed string
     */
    public static String transformPaymentOptionsEndpoint(ChubEndpoints endpoint, PaymentOptionType type) {
        return endpoint.getStub().replaceAll("\\{type\\}", type.name()).replaceAll("\\{.*?\\}", "(.*)");
    }
}
