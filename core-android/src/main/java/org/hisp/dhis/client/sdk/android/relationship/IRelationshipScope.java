package org.hisp.dhis.client.sdk.android.relationship;


import org.hisp.dhis.client.sdk.models.relationship.Relationship;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.List;

import rx.Observable;

public interface IRelationshipScope {

    Observable<Relationship> get(long id);

    Observable<List<Relationship>> list();

    Observable<List<Relationship>> list(TrackedEntityInstance object);

    Observable<Boolean> save(Relationship object);

    Observable<Boolean> remove(Relationship object);
}
