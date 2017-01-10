package org.hisp.dhis.android.core.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Pager {
    private final int page;
    private final int pageCount;
    private final int pageSize;
    private final int total;

    @JsonCreator
    public Pager(@JsonProperty("page") int page,
                 @JsonProperty("pageCount") int pageCount,
                 @JsonProperty("pageSize") int pageSize,
                 @JsonProperty("total") int total) {
        this.page = page;
        this.pageCount = pageCount;
        this.pageSize = pageSize;
        this.total = total;
    }

    public int page() {
        return page;
    }

    public int pageCount() {
        return pageCount;
    }

    public int pageSize() {
        return pageSize;
    }

    public int total() {
        return total;
    }
}
