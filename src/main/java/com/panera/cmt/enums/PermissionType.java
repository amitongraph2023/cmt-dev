package com.panera.cmt.enums;

public enum PermissionType {
    ADMIN("Admin")
    , CBSS("CBSS")
    , COFFEE("Coffee")
    , READONLY_CMT("Read Only")
    , SALES_ADMIN("Sales Administrator")
    , SECURITY("Security")
    ;

    private String displayName;

    PermissionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PermissionType getByDisplayName(String displayName) {
        for (PermissionType value : values()) {
            if (value.displayName.equals(displayName)) {
                return value;
            }
        }

        return null;
    }
}
