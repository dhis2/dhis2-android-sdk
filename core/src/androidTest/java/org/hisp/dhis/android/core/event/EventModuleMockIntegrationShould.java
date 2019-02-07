package org.hisp.dhis.android.core.event;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class EventModuleMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
        downloadEvents();
    }

    @Test
    public void allow_access_to_all_events_without_children() {
        List<Event> events = d2.eventModule().events.get();
        assertThat(events.size(), is(1));
        for (Event event: events) {
            assertThat(event.uid(), is("V1CerIi3sdL"));
            assertThat(event.organisationUnit(), is("DiszpKrYNg8"));
            assertThat(event.programStage(), is("dBwrot7S420"));
            assertThat(event.trackedEntityDataValues() == null, is(true));
        }
    }

    @Test
    public void allow_access_to_one_event_without_children() {
        Event event = d2.eventModule().events.uid("V1CerIi3sdL").get();
        assertThat(event.uid(), is("V1CerIi3sdL"));
        assertThat(event.organisationUnit(), is("DiszpKrYNg8"));
        assertThat(event.programStage(), is("dBwrot7S420"));
        assertThat(event.trackedEntityDataValues() == null, is(true));
    }
}