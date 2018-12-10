package org.hisp.dhis.android.core.event;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class EventQueryShould {

    @Test
    public void create_event_query_successfully() {
        EventQuery eventQuery = EventQuery.builder()
                .pageSize(50)
                .build();

        assertThat(eventQuery, is(not(nullValue())));
    }
}
