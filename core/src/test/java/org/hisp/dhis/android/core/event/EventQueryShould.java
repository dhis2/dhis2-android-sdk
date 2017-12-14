package org.hisp.dhis.android.core.event;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class EventQueryShould {
    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_if_limit_page_is_greater_than_page_size() {
        EventQuery.Builder
                .create()
                .withPageSize(50)
                .withPageLimit(51)
                .build();
    }

    @Test
    public void create_event_query_successfully() {
        EventQuery eventQuery = EventQuery.Builder
                .create()
                .withPageSize(51)
                .withPageLimit(50)
                .build();

        assertThat(eventQuery, is(not(nullValue())));
    }
}
