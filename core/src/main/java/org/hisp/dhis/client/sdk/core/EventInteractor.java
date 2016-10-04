package org.hisp.dhis.client.sdk.core;

public interface EventInteractor {
    EventStore store();

    EventApi api();
}
