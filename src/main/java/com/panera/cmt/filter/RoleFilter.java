package com.panera.cmt.filter;

import com.panera.cmt.entity.Endpoint;
import com.panera.cmt.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.panera.cmt.filter.HttpLoggingFilter.doLogAndReturn;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@Profile("!test")
@Slf4j
public class RoleFilter implements Filter {

    private static final List<Role> ROLES_ADMIN = singletonList(Role.ADMIN);
    private static final List<Role> ROLES_ALL = asList(Role.ADMIN, Role.CBSS, Role.CBSS_MANAGER, Role.COFFEE, Role.PROD_SUPPORT, Role.READ_ONLY, Role.SALES_ADMIN, Role.SECURITY);
    private static final List<Role> ROLES_CBSS = asList(Role.ADMIN, Role.CBSS, Role.CBSS_MANAGER, Role.COFFEE, Role.PROD_SUPPORT, Role.READ_ONLY, Role.SECURITY);
    private static final List<Role> ROLES_ESCALATION = asList(Role.ADMIN, Role.CBSS_MANAGER, Role.PROD_SUPPORT);
    private static final List<Role> ROLES_PROD_SUPPORT = asList(Role.ADMIN, Role.PROD_SUPPORT);

    private static final List<String> ALL_METHODS = emptyList();
    private static final List<String> GET = singletonList("GET");
    private static final List<String> POST = singletonList("POST");

    private List<Endpoint> endpointRoles = asList(
            new Endpoint(".*/api/v./app-config.*", ALL_METHODS, ROLES_ADMIN),
            new Endpoint(".*/api/v./cache.*", ALL_METHODS, ROLES_ADMIN),
            new Endpoint(".*/api/v./authentication", asList("DELETE", "GET"), ROLES_ALL),
            new Endpoint(".*/api/v./static/chub/.*", GET, ROLES_ALL),
            new Endpoint(".*/api/v./unsubscribe.*", POST, ROLES_CBSS),
            new Endpoint(".*/api/v./catering.*", ALL_METHODS, ROLES_ALL),
            new Endpoint(".*/api/v./customer/search", ALL_METHODS, ROLES_ALL),
            new Endpoint(".*/api/v./customer/[0-9\\-]*/address.*", ALL_METHODS, ROLES_CBSS),
            new Endpoint(".*/api/v./customer/[0-9\\-]*/details", GET, ROLES_ALL),
            new Endpoint(".*/api/v./customer/[0-9\\-]*/email", POST, ROLES_CBSS),
            new Endpoint(".*/api/v./customer/[0-9\\-]*/email", GET, ROLES_ALL),
            new Endpoint(".*/api/v./customer/[0-9\\-]*/email.*", ALL_METHODS, ROLES_CBSS),
            new Endpoint(".*/api/v./customer/[0-9\\-]*/mfa.*", ALL_METHODS, ROLES_ALL),
            new Endpoint(".*/api/v./customer/[0-9\\-]*/mfa/sms.*", POST, ROLES_CBSS),
            new Endpoint(".*/api/v./customer/[0-9\\-]*/password.*", POST, ROLES_CBSS),
            new Endpoint(".*/api/v./customer/[0-9\\-]*/password/admin/*", POST, ROLES_CBSS),
            new Endpoint(".*/api/v./customer/[0-9\\-]*/password/wotd*", GET, ROLES_CBSS),
            new Endpoint(".*/api/v./customer/[0-9\\-]*/loyalty.*", GET, ROLES_ALL),
            new Endpoint(".*/api/v./customer/[0-9\\-]*/paymentoptions.*", GET, ROLES_ALL),
            new Endpoint(".*/api/v./customer/[0-9\\-]*/paymentoptions.*", ALL_METHODS, ROLES_CBSS),
            new Endpoint(".*/api/v./customer/[0-9\\-]*/phone.*", ALL_METHODS, ROLES_CBSS),
            new Endpoint(".*/api/v./customer/[0-9\\-]*/status", POST, ROLES_CBSS),
            new Endpoint(".*/api/v./customer/[0-9\\-]*/subscriptions.*", ALL_METHODS, ROLES_CBSS),
            new Endpoint(".*/api/v./customer/[0-9\\-]*/userpreferences.*", ALL_METHODS, ROLES_CBSS),
            new Endpoint(".*/api/v./customer/[0-9\\-]*", GET, ROLES_ALL),
            new Endpoint(".*/api/v./customer/[0-9\\-]*", ALL_METHODS, ROLES_CBSS),
            new Endpoint(".*/api/v./paytronix/[0-9\\-]*", ALL_METHODS, ROLES_ALL),
            new Endpoint(".*/api/v./paytronixEsb/[0-9\\-]*", ALL_METHODS, ROLES_ALL),
            new Endpoint(".*/api/v./png/*", ALL_METHODS, ROLES_ALL),
            new Endpoint(".*/api/v./sso/*", ALL_METHODS, ROLES_ALL),
            new Endpoint(".*/api/v./subscriptionService/*", ALL_METHODS, ROLES_ALL),
            new Endpoint(".*/api/v./lmt/*", ALL_METHODS, ROLES_CBSS),
            new Endpoint(".*/api/v./ui-config/*", GET, ROLES_ALL)
    );

    private boolean isForbidden(String endpoint, String method, Role role) {
        Pattern p = Pattern.compile(".*/api/v./authentication");
        Matcher m = p.matcher(endpoint.toLowerCase());
        if (m.find() && method.equalsIgnoreCase("POST")) {
            return false;
        }

        for (Endpoint endpointRole : endpointRoles) {
            p = Pattern.compile(endpointRole.getEndpoint().toLowerCase());
            m = p.matcher(endpoint.toLowerCase());
            if (m.find() && (endpointRole.getMethods().size() == 0 || endpointRole.getMethods().contains(method))) {
                return (endpointRole.getRoles().size() > 0 && (!endpointRole.getRoles().contains(role)));
            }
        }

        return true;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        Date requestStartTime = new Date();

        // Cast the request and response
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (request.getServletPath() != null && request.getServletPath().startsWith("/api") && AuthenticatedUserManager.getAuthenticatedUser() != null && isForbidden(request.getServletPath(), request.getMethod(), AuthenticatedUserManager.getAuthenticatedUser().getRole())) {
            // The user does not have the role to use this endpoint, preempt the request and send an forbidden response
            doLogAndReturn(request, response, String.format("Forbidden access to endpoint=%s method=%s role=%s", request.getRequestURI(), request.getMethod(), AuthenticatedUserManager.getAuthenticatedUser().getRole()), HttpServletResponse.SC_FORBIDDEN, "Insufficient permissions", requestStartTime);
        } else {
            chain.doFilter(request, res);
        }
    }

    @Override
    public void destroy() {}

    @Override
    public void init(FilterConfig arg0) throws ServletException {}
}
