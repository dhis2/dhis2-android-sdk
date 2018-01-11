package org.hisp.dhis.android.core.data.audit;

import java.util.Date;

@SuppressWarnings("PMD.UnusedLocalVariable")
public class MetadataAudit<T> {
    private int id;

    private final Date createdAt = new Date();

    private String createdBy;

    private String klass;

    private String uid;

    private String code;

    private AuditType type;

    private T value;

    public int getId() {
        return id;
    }

    public Date getCreatedAt() {
        return new Date(createdAt.getTime());
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getKlass() {
        return klass;
    }

    public String getUid() {
        return uid;
    }

    public String getCode() {
        return code;
    }

    public AuditType getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "MetadataAudit{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", createdBy='" + createdBy + '\'' +
                ", klass=" + klass +
                ", uid='" + uid + '\'' +
                ", code='" + code + '\'' +
                ", type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}