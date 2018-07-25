package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;

import java.util.List;

public class EventsPager {

    @JsonProperty("pager")
    @Column(name = "pager")
    Pager pager;

    @JsonProperty("events")
    @Column(name = "events")
    List<Event> events;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
