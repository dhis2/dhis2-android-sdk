package org.hisp.dhis.android.core.dataelement;

import java.util.HashSet;
import java.util.Set;

class DataElementQuery {
    private final Set<String> uIds;

    public DataElementQuery(Set<String> uIds) {
        this.uIds = uIds;
    }

    public Set<String> getUIds() {
        return uIds;
    }

    public static class Builder {
        private Set<String> uIds = new HashSet<>();

        private Builder() {
        }

        public static DataElementQuery.Builder create() {
            return new DataElementQuery.Builder();
        }

        public DataElementQuery.Builder withUIds(Set<String> uIds) {
            this.uIds = uIds;
            return this;
        }

        public DataElementQuery build() {
            return new DataElementQuery(uIds);
        }
    }
}
