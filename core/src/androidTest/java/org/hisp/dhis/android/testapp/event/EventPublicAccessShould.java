package org.hisp.dhis.android.testapp.event;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class EventPublicAccessShould extends BasePublicAccessShould<Event> {

    @Mock
    private Event object;

    @Override
    public Event object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        Event.create(null);
    }

    @Override
    public void has_public_builder_method() {
        Event.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}