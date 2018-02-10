package org.hisp.dhis.android.core.audit;

public final class SyncedMetadataMapper {
    public static SyncedMetadata map(Class<?> klass, MetadataAudit metadataAudit) {
        return SyncedMetadata.builder()
                .uid(metadataAudit.getUid())
                .klass(klass.getName())
                .type(metadataAudit.getType())
                .build();
    }

    private SyncedMetadataMapper() {
    }
}
