package org.hisp.dhis.android.core.audit;

public enum AuditType {
    CREATE("create"), UPDATE("update"), DELETE("delete");

    private final String value;

    AuditType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
