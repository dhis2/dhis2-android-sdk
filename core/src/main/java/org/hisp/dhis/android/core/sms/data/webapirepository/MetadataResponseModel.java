package org.hisp.dhis.android.core.sms.data.webapirepository;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_MetadataResponseModel.Builder.class)
public abstract class MetadataResponseModel {
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
    @JsonDeserialize(builder = AutoValue_MetadataResponseModel_MetadataSystemInfo.Builder.class)
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
    @JsonDeserialize(builder = AutoValue_MetadataResponseModel_MetadataId.Builder.class)
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

        public abstract MetadataResponseModel.Builder system(MetadataSystemInfo systemInfo);

        public abstract MetadataResponseModel.Builder categoryOptionCombos(List<MetadataId> ids);

        public abstract MetadataResponseModel.Builder organisationUnits(List<MetadataId> ids);

        public abstract MetadataResponseModel.Builder dataElements(List<MetadataId> ids);

        public abstract MetadataResponseModel.Builder users(List<MetadataId> ids);

        public abstract MetadataResponseModel.Builder trackedEntityTypes(List<MetadataId> ids);

        public abstract MetadataResponseModel.Builder trackedEntityAttributes(List<MetadataId> ids);

        public abstract MetadataResponseModel.Builder programs(List<MetadataId> ids);

        public abstract MetadataResponseModel build();
    }
}