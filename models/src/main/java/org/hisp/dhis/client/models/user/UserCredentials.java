package org.hisp.dhis.client.models.user;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.models.common.BaseIdentifiableObject;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

// TODO: Tests
@AutoValue
@JsonDeserialize(builder = AutoValue_UserCredentials.Builder.class)
public abstract class UserCredentials extends BaseIdentifiableObject {
    private static final String JSON_PROPERTY_USER_ROLES = "userRoles";
    private static final String JSON_PROPERTY_USERNAME = "username";

    @JsonProperty(JSON_PROPERTY_USERNAME)
    public abstract String username();

    @JsonProperty(JSON_PROPERTY_USER_ROLES)
    public abstract List<UserRole> userRoles();

    public static Builder builder() {
        return new AutoValue_UserCredentials.Builder();
    }

    @AutoValue.Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {

        @JsonProperty(JSON_PROPERTY_USER_ROLES)
        public abstract Builder userRoles(List<UserRole> userRoles);

        @JsonProperty(JSON_PROPERTY_USERNAME)
        public abstract Builder username(String username);

        // internal, not exposed
        abstract List<UserRole> userRoles();

        abstract UserCredentials autoBuild();

        public UserCredentials build() {
            if (userRoles() != null) {
                userRoles(Collections.unmodifiableList(userRoles()));
            }

            return autoBuild();
        }
    }

}
