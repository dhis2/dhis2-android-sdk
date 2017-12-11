package org.hisp.dhis.android.core.event;

import java.util.HashSet;
import java.util.Set;

public class EventQuery {
    private final Set<String> uIds;
    private final int page;
    private final int pageSize;
    private final boolean paging;
    private final String orgUnit;
    private final String program;
    private final String trackedEntityInstance;

    private final int pageLimit;

    public EventQuery(boolean paging, int page, int pageSize,
            String orgUnit, String program, String trackedEntityInstance, Set<String> uIds,
            int pageLimit) {
        this.paging = paging;
        this.page = page;
        this.pageSize = pageSize;
        this.orgUnit = orgUnit;
        this.program = program;
        this.trackedEntityInstance = trackedEntityInstance;
        this.uIds = uIds;
        this.pageLimit = pageLimit;
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

    public String getOrgUnit() {
        return orgUnit;
    }

    public String getProgram() {
        return program;
    }

    public String getTrackedEntityInstance() {
        return trackedEntityInstance;
    }

    public int getPageLimit() {
        return pageLimit;
    }

    public static class Builder {
        private int page = 1;
        private int pageSize = 50;
        private boolean paging;
        private String orgUnit;
        private String program;
        private String trackedEntityInstance;
        int pageLimit;

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

        public Builder withOrgUnit(String orgUnit) {
            this.orgUnit = orgUnit;
            return this;
        }

        public Builder withProgram(String program) {
            this.program = program;
            return this;
        }

        public Builder withTrackedEntityInstance(String trackedEntityInstance) {
            this.trackedEntityInstance = trackedEntityInstance;
            return this;
        }

        public Builder withUIds(Set<String> uIds) {
            this.uIds = uIds;
            return this;
        }

        public Builder withPageLimit(int pageLimit) {
            this.pageLimit = pageLimit;
            return this;
        }

        public EventQuery build() {
            if (pageLimit > pageSize) {
                throw new IllegalArgumentException(
                        "pageLimit can not be more greater than pageSize");
            }

            return new EventQuery(paging, page, pageSize,
                    orgUnit, program, trackedEntityInstance, uIds, pageLimit);
        }
    }
}