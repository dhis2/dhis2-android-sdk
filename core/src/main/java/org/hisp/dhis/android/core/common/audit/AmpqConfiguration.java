package org.hisp.dhis.android.core.common.audit;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class AmpqConfiguration {
    public abstract String host();

    public abstract String virtualHost();

    public abstract String username();

    public abstract String password();

    public abstract int port();

    static Builder builder() {
        return new AutoValue_AmpqConfiguration.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder {
        public abstract AmpqConfiguration.Builder setHost(String value);

        public abstract AmpqConfiguration.Builder setVirtualHost(String value);

        public abstract AmpqConfiguration.Builder setUsername(String value);

        public abstract AmpqConfiguration.Builder setPassword(String value);

        public abstract AmpqConfiguration.Builder setPort(int value);

        public abstract AmpqConfiguration build();
    }
}

