package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.api.OuMode;

import java.util.ArrayList;
import java.util.Collection;

public class TeiQuery {

    private final int page;
    private final int pageSize;
    private final boolean paging;
    private final Collection<String> orgUnits;
    private final OuMode ouMode;

    @Nullable
    private final Collection<String> uIds;

    public TeiQuery(boolean paging, int page, int pageSize, Collection<String> orgUnits, OuMode ouMode) {
        this.paging = paging;
        this.page = page;
        this.pageSize = pageSize;
        this.orgUnits = orgUnits;
        this.ouMode = ouMode;
        uIds = null;
    }

    public TeiQuery(boolean paging, int page, int pageSize,
                    Collection<String> orgUnits, @Nullable Collection<String> uIds, OuMode ouMode) {
        this.paging = paging;
        this.page = page;
        this.pageSize = pageSize;
        this.orgUnits = orgUnits;
        this.uIds = uIds;
        this.ouMode = ouMode;
    }

    @Nullable
    public Collection<String> getUIds() {
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

    public Collection<String> getOrgUnits() {
        return orgUnits;
    }

    public static class Builder {
        private int page = 1;
        private int pageSize = 50;
        private boolean paging;
        private Collection<String> orgUnits;
        OuMode ouMode = OuMode.SELECTED;

        private Collection<String> uIds = new ArrayList<>();

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

        public TeiQuery.Builder withOrgUnits(Collection<String> orgUnits) {
            this.orgUnits = orgUnits;
            return this;
        }

        public TeiQuery.Builder withUIds(Collection<String> uIds) {
            this.uIds = uIds;
            return this;
        }

        public TeiQuery.Builder withOuMode(OuMode ouMode) {
            this.ouMode = ouMode;
            return this;
        }

        public TeiQuery build() {
            return new TeiQuery(paging, page, pageSize, orgUnits, uIds, ouMode);
        }
    }
}
