package org.hisp.dhis.android.sdk.trackedentity;

import org.hisp.dhis.java.sdk.models.trackedentity.TrackedEntityAttribute;

import java.util.List;

import rx.Observable;

public interface ITrackedEntityAttributeScope {
    Observable<TrackedEntityAttribute> get(long id);

    Observable<TrackedEntityAttribute> get(String uid);

    Observable<List<TrackedEntityAttribute>> list();

    Observable<Boolean> save(TrackedEntityAttribute object);

    Observable<Boolean> remove(TrackedEntityAttribute object);
}
