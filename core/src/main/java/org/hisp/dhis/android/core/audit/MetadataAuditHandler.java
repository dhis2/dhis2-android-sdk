package org.hisp.dhis.android.core.audit;

public interface MetadataAuditHandler {
    void handle(MetadataAudit metadataAudit) throws Exception;
}
