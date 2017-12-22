package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryOption;

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

    @Nullable
    private final CategoryOption categoryOption;

    @Nullable
    private final CategoryCombo categoryCombo;

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
        this.categoryCombo = null;
        this.categoryOption = null;
    }

    public EventQuery(boolean paging, int page, int pageSize,
            String orgUnit, String program, String trackedEntityInstance, Set<String> uIds,
            int pageLimit,
            @Nullable CategoryCombo categoryCombo,
            @Nullable CategoryOption categoryOption) {
        this.paging = paging;
        this.page = page;
        this.pageSize = pageSize;
        this.orgUnit = orgUnit;
        this.program = program;
        this.trackedEntityInstance = trackedEntityInstance;
        this.uIds = uIds;
        this.pageLimit = pageLimit;
        this.categoryCombo = categoryCombo;
        this.categoryOption = categoryOption;
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

    @Nullable
    public CategoryOption getCategoryOption() {
        return categoryOption;
    }

    @Nullable
    public CategoryCombo getCategoryCombo() {
        return categoryCombo;
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

        @Nullable
        private CategoryOption categoryOption;

        @Nullable
        private CategoryCombo categoryCombo;

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

        public Builder withCategoryComboAndCategoryOption(@NonNull CategoryCombo categoryCombo,
                CategoryOption categoryOption) {
            this.categoryOption = categoryOption;
            this.categoryCombo = categoryCombo;
            return this;
        }

        public EventQuery build() {
            if (pageLimit > pageSize) {
                throw new IllegalArgumentException(
                        "pageLimit can not be more greater than pageSize");
            }

            return new EventQuery(paging, page, pageSize,
                    orgUnit, program, trackedEntityInstance, uIds, pageLimit,
                    categoryCombo, categoryOption);
        }
    }
}