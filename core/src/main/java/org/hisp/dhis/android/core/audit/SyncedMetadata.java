package org.hisp.dhis.android.core.audit;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SyncedMetadata {
    public abstract String uid();

    public abstract String klass();

    public abstract AuditType type();

    static SyncedMetadata.Builder builder() {
        return new AutoValue_SyncedMetadata.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder {
        public abstract SyncedMetadata.Builder uid(String value);

        public abstract SyncedMetadata.Builder klass(String value);

        public abstract SyncedMetadata.Builder type(AuditType value);

        public abstract SyncedMetadata build();
    }
}
