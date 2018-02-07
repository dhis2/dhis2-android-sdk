package org.hisp.dhis.android.core.trackedentity;

import java.util.HashSet;
import java.util.Set;

public class TrackedEntityAttributeQuery {
    private final Set<String> uIds;


    public TrackedEntityAttributeQuery(Set<String> uIds) {
        this.uIds = uIds;
    }

    public Set<String> getUIds() {
        return uIds;
    }

    public static class Builder {
        private Set<String> uIds = new HashSet<>();

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder withUIds(Set<String> uIds) {
            this.uIds = uIds;
            return this;
        }

        public TrackedEntityAttributeQuery build() {
            return new TrackedEntityAttributeQuery(uIds);
        }
    }

}
