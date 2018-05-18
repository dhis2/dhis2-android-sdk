package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.api.OuMode;

import java.util.HashSet;
import java.util.Set;

public class TeiQuery {

    private final int page;
    private final int pageSize;
    private final boolean paging;
    private final Set<String> orgUnits;
    private final int pageLimit;
    private final OuMode ouMode;

    @Nullable
    private final Set<String> uIds;


    public TeiQuery(boolean paging, int page, int pageSize, Set<String> orgUnits, int pageLimit, OuMode ouMode) {
        this.paging = paging;
        this.page = page;
        this.pageSize = pageSize;
        this.orgUnits = orgUnits;
        this.pageLimit = pageLimit;
        this.ouMode = ouMode;
        uIds = null;
    }

    public TeiQuery(boolean paging, int page, int pageSize,
                    Set<String> orgUnits, @Nullable Set<String> uIds, int pageLimit, OuMode ouMode) {
        this.paging = paging;
        this.page = page;
        this.pageSize = pageSize;
        this.orgUnits = orgUnits;
        this.uIds = uIds;
        this.pageLimit = pageLimit;
        this.ouMode = ouMode;
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

    public OuMode getOuMode() {
        return ouMode;
    }

    public Set<String> getOrgUnits() {
        return orgUnits;
    }

    public int getPageLimit() {
        return pageLimit;
    }

    public static class Builder {
        private int page = 1;
        private int pageSize = 50;
        private boolean paging;
        private Set<String> orgUnits;
        int pageLimit = 50;
        OuMode ouMode = OuMode.SELECTED;

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

        public TeiQuery.Builder withOrgUnits(Set<String> orgUnits) {
            this.orgUnits = orgUnits;
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

        public TeiQuery.Builder withOuMode(OuMode ouMode) {
            this.ouMode = ouMode;
            return this;
        }

        public TeiQuery build() {
            if (pageLimit > pageSize) {
                throw new IllegalArgumentException(
                        "pageLimit can not be more greater than pageSize");
            }

            return new TeiQuery(paging, page, pageSize, orgUnits, uIds, pageLimit, ouMode);
        }
    }
}
