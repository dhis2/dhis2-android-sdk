package org.hisp.dhis.android.core.sms.data.webapirepository.internal;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import java.util.Date;
import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_MetadataResponse.Builder.class)
public abstract class MetadataResponse {
    public abstract MetadataSystemInfo system();

    @Nullable
    public abstract List<MetadataId> categoryOptionCombos();

    @Nullable
    public abstract List<MetadataId> organisationUnits();

    @Nullable
    public abstract List<MetadataId> dataElements();

    @Nullable
    public abstract List<MetadataId> users();

    @Nullable
    public abstract List<MetadataId> trackedEntityTypes();

    @Nullable
    public abstract List<MetadataId> trackedEntityAttributes();

    @Nullable
    public abstract List<MetadataId> programs();

    @AutoValue
    @JsonDeserialize(builder = AutoValue_MetadataResponse_MetadataSystemInfo.Builder.class)
    public abstract static class MetadataSystemInfo {
        public abstract Date date();

        @AutoValue.Builder
        @JsonPOJOBuilder(withPrefix = "")
        public static abstract class Builder {

            public abstract MetadataSystemInfo.Builder date(Date date);

            public abstract MetadataSystemInfo build();
        }
    }

    @AutoValue
    @JsonDeserialize(builder = AutoValue_MetadataResponse_MetadataId.Builder.class)
    public abstract static class MetadataId {
        public abstract String id();

        @AutoValue.Builder
        @JsonPOJOBuilder(withPrefix = "")
        public static abstract class Builder {

            public abstract MetadataId.Builder id(String id);

            public abstract MetadataId build();
        }
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder {

        public abstract MetadataResponse.Builder system(MetadataSystemInfo systemInfo);

        public abstract MetadataResponse.Builder categoryOptionCombos(List<MetadataId> ids);

        public abstract MetadataResponse.Builder organisationUnits(List<MetadataId> ids);

        public abstract MetadataResponse.Builder dataElements(List<MetadataId> ids);

        public abstract MetadataResponse.Builder users(List<MetadataId> ids);

        public abstract MetadataResponse.Builder trackedEntityTypes(List<MetadataId> ids);

        public abstract MetadataResponse.Builder trackedEntityAttributes(List<MetadataId> ids);

        public abstract MetadataResponse.Builder programs(List<MetadataId> ids);

        public abstract MetadataResponse build();
    }
}