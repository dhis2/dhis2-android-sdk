package org.hisp.dhis.client.sdk.android.trackedentity;

import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.List;

import rx.Observable;

public interface ITrackedEntityAttributeValueScope {
    Observable<TrackedEntityAttributeValue> get(long id);

    Observable<TrackedEntityAttributeValue> get(TrackedEntityInstance trackedEntityInstance,
                                                TrackedEntityAttribute trackedEntityAttribute);

    Observable<List<TrackedEntityAttributeValue>> list();

    Observable<List<TrackedEntityAttributeValue>> list(TrackedEntityInstance trackedEntityInstance);

    Observable<List<TrackedEntityAttributeValue>> list(Enrollment enrollment);

    Observable<Boolean> save(TrackedEntityAttributeValue object);

    Observable<Boolean> remove(TrackedEntityAttributeValue object);

}
