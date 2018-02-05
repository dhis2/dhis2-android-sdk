package org.hisp.dhis.android.core.dataelement;

import java.util.Set;

class DataElementQuery {
    private final Set<String> uIds;

    public DataElementQuery(Set<String> uIds) {
        this.uIds = uIds;
    }

    public Set<String> getUIds() {
        return uIds;
    }
}
