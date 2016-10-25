package org.hisp.dhis.client.models.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.models.program.Program;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

// TODO: Tests
@AutoValue
@JsonDeserialize(builder = AutoValue_UserRole.Builder.class)
public abstract class UserRole extends BaseIdentifiableObject {
    private static final String JSON_PROPERTY_PROGRAMS = "programs";

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAMS)
    @Nullable
    public abstract List<Program> programs();

    public static Builder builder() {
        return new AutoValue_UserRole.Builder();
    }

    @AutoValue.Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {

        @JsonProperty(JSON_PROPERTY_PROGRAMS)
        public abstract Builder programs(@Nullable List<Program> programs);

        // internal, not exposed
        abstract List<Program> programs();

        abstract UserRole autoBuild();

        public UserRole build() {
            if (programs() != null) {
                programs(Collections.unmodifiableList(programs()));
            }

            return autoBuild();
        }
    }

}