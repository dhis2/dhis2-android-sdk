package org.hisp.dhis.android.core.data.audit;


import java.util.Date;

public class MetadataAudit<T> {
    private int id;

    private Date createdAt = new Date();

    private String createdBy;

    private String klass;

    private String uid;

    private String code;

    private AuditType type;

    private T value;

    public MetadataAudit() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getKlass() {
        return klass;
    }

    public void setKlass(String klass) {
        this.klass = klass;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AuditType getType() {
        return type;
    }

    public void setType(AuditType type) {
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
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