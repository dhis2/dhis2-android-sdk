package org.hisp.dhis.client.sdk.android.trackedentity;

import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.List;

import rx.Observable;

public interface ITrackedEntityDataValueScope {
    Observable<TrackedEntityDataValue> get(long id);

    Observable<List<TrackedEntityDataValue>> list();

    Observable<Boolean> save(TrackedEntityDataValue object);

    Observable<Boolean> remove(TrackedEntityDataValue object);

    Observable<Boolean> add(TrackedEntityDataValue object);

    Observable<Boolean> update(TrackedEntityDataValue object);

    Observable<TrackedEntityDataValue> create(Event event, String dataElement, boolean providedElsewhere, String storedBy, String value);

    Observable<List<TrackedEntityDataValue>> list(Event event);

    Observable<TrackedEntityDataValue> get(DataElement dataElement, Event event);

    Observable<Void> send(String uid);

}
