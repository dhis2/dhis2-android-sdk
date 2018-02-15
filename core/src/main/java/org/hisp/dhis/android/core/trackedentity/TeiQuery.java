package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

public class TeiQuery {

    private final int page;
    private final int pageSize;
    private final boolean paging;
    private final String orgUnit;
    private final int pageLimit;

    @Nullable
    private final Set<String> uIds;


    public TeiQuery(boolean paging, int page, int pageSize,
                    String orgUnit, int pageLimit) {
        this.paging = paging;
        this.page = page;
        this.pageSize = pageSize;
        this.orgUnit = orgUnit;
        this.pageLimit = pageLimit;
        uIds = null;
    }

    public TeiQuery(boolean paging, int page, int pageSize,
                    String orgUnit, @Nullable Set<String> uIds, int pageLimit) {
        this.paging = paging;
        this.page = page;
        this.pageSize = pageSize;
        this.orgUnit = orgUnit;
        this.uIds = uIds;
        this.pageLimit = pageLimit;
    }

    @Nullable
    public Set<String> getUIds() {
        return uIds;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean isPaging() {
        return paging;
    }

    public String getOrgUnit() {
        return orgUnit;
    }

    public int getPageLimit() {
        return pageLimit;
    }

    public static class Builder {
        private int page = 1;
        private int pageSize = 50;
        private boolean paging;
        private String orgUnit;
        int pageLimit;

        private Set<String> uIds = new HashSet<>();

        private Builder() {
        }

        public static TeiQuery.Builder create() {
            return new TeiQuery.Builder();
        }

        public TeiQuery.Builder withPaging(boolean paging) {
            this.paging = paging;
            return this;
        }

        public TeiQuery.Builder withPage(int page) {
            this.page = page;
            return this;
        }

        public TeiQuery.Builder withPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public TeiQuery.Builder withOrgUnit(String orgUnit) {
            this.orgUnit = orgUnit;
            return this;
        }

        public TeiQuery.Builder withUIds(Set<String> uIds) {
            this.uIds = uIds;
            return this;
        }

        public TeiQuery.Builder withPageLimit(int pageLimit) {
            this.pageLimit = pageLimit;
            return this;
        }

        public TeiQuery build() {
            if (pageLimit > pageSize) {
                throw new IllegalArgumentException(
                        "pageLimit can not be more greater than pageSize");
            }

            return new TeiQuery(paging, page, pageSize,
                    orgUnit, uIds, pageLimit);
        }
    }
}
