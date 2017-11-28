package org.hisp.dhis.android.core.event;

import java.util.HashSet;
import java.util.Set;

public class EventQuery {
    private Set<String> uIds;
    private int page;
    private int pageSize;
    private boolean paging;

    public EventQuery(boolean paging, int page, int pageSize, Set<String> uIds) {
        this.paging = paging;
        this.page = page;
        this.pageSize = pageSize;
        this.uIds = uIds;
    }

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

    public static class Builder {
        private int page = 1;
        private int pageSize = 5;
        private boolean paging = false;
        private Set<String> uIds = new HashSet<>();

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder withPaging(boolean paging) {
            this.paging = paging;
            return this;
        }

        public Builder withPage(int page) {
            this.page = page;
            return this;
        }

        public Builder withPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder withUIds(Set<String> uIds) {
            this.uIds = uIds;
            return this;
        }

        public EventQuery build() {
            return new EventQuery(paging, page, pageSize, uIds);
        }
    }
}