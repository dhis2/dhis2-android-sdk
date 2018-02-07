package org.hisp.dhis.android.core.audit;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class MetadataAuditConnection {
    public abstract String host();

    public abstract String virtualHost();

    public abstract String username();

    public abstract String password();

    public abstract int port();

    static Builder builder() {
        return new AutoValue_MetadataAuditConnection.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder {
        public abstract MetadataAuditConnection.Builder setHost(String value);

        public abstract MetadataAuditConnection.Builder setVirtualHost(String value);

        public abstract MetadataAuditConnection.Builder setUsername(String value);

        public abstract MetadataAuditConnection.Builder setPassword(String value);

        public abstract MetadataAuditConnection.Builder setPort(int value);

        public abstract MetadataAuditConnection build();
    }
}

