package org.hisp.dhis.client.sdk.android.trackedentity;

import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;

import java.util.List;

import rx.Observable;

public interface ITrackedEntityAttributeScope {
    Observable<TrackedEntityAttribute> get(long id);

    Observable<TrackedEntityAttribute> get(String uid);

    Observable<List<TrackedEntityAttribute>> list();

    Observable<Boolean> save(TrackedEntityAttribute object);

    Observable<Boolean> remove(TrackedEntityAttribute object);

    Observable<Boolean> send();

}
