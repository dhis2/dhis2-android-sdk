package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;

public class Pager {

    @JsonProperty("page")
    @Column(name = "page")
    Integer page;

    @JsonProperty("pageCount")
    @Column(name = "pageCount")
    Integer pageCount;

    @JsonProperty("total")
    @Column(name = "total")
    Integer total;

    @JsonProperty("pageSize")
    @Column(name = "pageSize")
    Integer pageSize;
}
