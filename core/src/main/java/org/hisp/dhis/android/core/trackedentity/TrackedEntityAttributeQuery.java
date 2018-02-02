package org.hisp.dhis.android.core.trackedentity;

import static org.hisp.dhis.android.core.calls.Call.MAX_UIDS;

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
            if (uIds != null && uIds.size() > MAX_UIDS) {
                throw new IllegalArgumentException(
                        "Can't handle the amount of trackedEntityAttributes: "
                                + uIds.size() + ". " +
                                "Max size is: " + MAX_UIDS);
            }

            return new TrackedEntityAttributeQuery(uIds);
        }
    }

}
