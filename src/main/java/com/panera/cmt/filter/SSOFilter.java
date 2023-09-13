package com.panera.cmt.filter;

import com.panera.cmt.entity.Endpoint;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.service.IAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.panera.cmt.config.Constants.SSO_COOKIE_NAME;
import static com.panera.cmt.config.Constants.getExpirationDate;
import static com.panera.cmt.filter.HttpLoggingFilter.doLogAndReturn;
import static com.panera.cmt.util.SharedUtils.createCookie;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.springframework.web.util.WebUtils.getCookie;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 2)
@Profile("!test")
@Slf4j
public class SSOFilter implements Filter {

    private IAuthenticationService authenticationService;
    private List<Endpoint> openEndpoints = asList(
            new Endpoint(".*/api/v./authentication", singletonList("POST"))
    );

    @Autowired
    public void setAuthenticationService(IAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    private boolean isEndPointOpen(String endpoint, String method) {
        for (Endpoint openEndpoint : openEndpoints) {
            if (endpoint.toLowerCase().matches(openEndpoint.getEndpoint()) && (openEndpoint.getMethods().size() == 0 || openEndpoint.getMethods().contains(method))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        Date requestStartTime = new Date();

        try {
            // Cast the request and response
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;

            Cookie ssoCookie = getCookie(request, SSO_COOKIE_NAME);
            if (ssoCookie != null) {
                // Attempt to get the SSO session
                AuthenticatedUser authenticatedUser = authenticationService.getAuthenticatedUser(ssoCookie.getValue()).orElse(null);

                // Check if there is an active sso session
                if (authenticatedUser != null) {
                    // Set the portal user
                    AuthenticatedUserManager.setAuthenticatedUser(authenticatedUser);

                    // Update the TTL for the session
                    authenticatedUser.setExpirationDate(getExpirationDate());
                    authenticationService.updateSession(authenticatedUser.getId(), authenticatedUser);
                } else {
                    // Delete the orphan cookie
                    response.addCookie(createCookie(SSO_COOKIE_NAME, ssoCookie.getValue(), 0)); // 0 deletes the cookie
                }
            }

            // Check if the user is authorized or the endpoint is open
            if (request.getServletPath() != null && request.getServletPath().startsWith("/api/v") && AuthenticatedUserManager.getAuthenticatedUser() == null && !isEndPointOpen(request.getServletPath(), request.getMethod())) {
                // The user is not authorized and the endpoint is not open, preempt the request and send an unauthorized response
                doLogAndReturn(request, response, String.format("Unauthorized access to endpoint=%s method=%s", request.getRequestURI(), request.getMethod()), HttpServletResponse.SC_UNAUTHORIZED, "Not Logged In", requestStartTime);
            } else {
                // The user is authorized or the endpoint is open, continue with the request
                chain.doFilter(request, res);
            }
        } finally {
            AuthenticatedUserManager.cleanUp();
        }
    }

    @Override
    public void destroy() {}

    @Override
    public void init(FilterConfig arg0) throws ServletException {}
}
