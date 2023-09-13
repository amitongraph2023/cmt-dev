package com.panera.cmt.filter;

import com.panera.cmt.enums.Role;
import com.panera.cmt.mongo.entity.AuthenticatedUser;

public class AuthenticatedUserManager {

    private static final ThreadLocal<AuthenticatedUser> context = new ThreadLocal<>();

    public static void setAuthenticatedUser(AuthenticatedUser authenticatedUser) {
        context.set(authenticatedUser);
    }

    public static AuthenticatedUser getAuthenticatedUser() {
        return context.get();
    }

    public static void cleanUp() {
        context.remove();
    }

    public static boolean canViewAllDetails() {
        AuthenticatedUser authenticatedUser = getAuthenticatedUser();
        return authenticatedUser != null && authenticatedUser.getRole() != null &&
                (authenticatedUser.getRole().equals(Role.ADMIN)
                        || authenticatedUser.getRole().equals(Role.CBSS)
                        || authenticatedUser.getRole().equals(Role.CBSS_MANAGER)
                        || authenticatedUser.getRole().equals(Role.PROD_SUPPORT)
                        || authenticatedUser.getRole().equals(Role.READ_ONLY)
                        || authenticatedUser.getRole().equals(Role.SECURITY)
                );
    }

    public static boolean hasAdminRole() {
        AuthenticatedUser authenticatedUser = getAuthenticatedUser();
        return authenticatedUser != null && authenticatedUser.getRole() != null && authenticatedUser.getRole().equals(Role.ADMIN);
    }
    public static boolean hasCBSSRole() {
        AuthenticatedUser authenticatedUser = getAuthenticatedUser();
        return authenticatedUser != null && authenticatedUser.getRole() != null && authenticatedUser.getRole().equals(Role.CBSS);
    }
    public static boolean hasCBSSManagerRole() {
        AuthenticatedUser authenticatedUser = getAuthenticatedUser();
        return authenticatedUser != null && authenticatedUser.getRole() != null && authenticatedUser.getRole().equals(Role.CBSS_MANAGER);
    }
    public static boolean hasCoffeeRole() {
        AuthenticatedUser authenticatedUser = getAuthenticatedUser();
        return authenticatedUser != null && authenticatedUser.getRole() != null && authenticatedUser.getRole().equals(Role.COFFEE);
    }
    public static boolean hasProdSupportRole() {
        AuthenticatedUser authenticatedUser = getAuthenticatedUser();
        return authenticatedUser != null && authenticatedUser.getRole() != null && authenticatedUser.getRole().equals(Role.PROD_SUPPORT);
    }
    public static boolean hasReadOnlyRole() {
        AuthenticatedUser authenticatedUser = getAuthenticatedUser();
        return authenticatedUser != null && authenticatedUser.getRole() != null && authenticatedUser.getRole().equals(Role.READ_ONLY);
    }
    public static boolean hasSalesAdminRole() {
        AuthenticatedUser authenticatedUser = getAuthenticatedUser();
        return authenticatedUser != null && authenticatedUser.getRole() != null && authenticatedUser.getRole().equals(Role.SALES_ADMIN);
    }
    public static boolean hasSecurityRole() {
        AuthenticatedUser authenticatedUser = getAuthenticatedUser();
        return authenticatedUser != null && authenticatedUser.getRole() != null && authenticatedUser.getRole().equals(Role.SECURITY);
    }
}