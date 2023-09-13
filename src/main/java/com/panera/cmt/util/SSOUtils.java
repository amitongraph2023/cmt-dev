package com.panera.cmt.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SSOUtils {

    /**
     * Appends logout url onto baseUrl passed in
     * @param {String} ssoBaseUrl
     * @return {String} full logout url
     */
    public static String ssoLogout(String ssoBaseUrl)  {
        return ssoBaseUrl + "/token/:accessToken";
    }

    /**
     * Append login url onto baseURL passed in
     * @param {String} ssoBaseUrl
     * @return {String} full login url
     */
    public static String ssoSpoof(String ssoBaseUrl)  {
        return ssoBaseUrl + "/login/impersonate/:customerId";
    }
}
