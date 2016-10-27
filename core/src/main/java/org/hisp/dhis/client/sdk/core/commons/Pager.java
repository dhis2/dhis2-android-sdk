package org.hisp.dhis.client.sdk.core.commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Pager {
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
