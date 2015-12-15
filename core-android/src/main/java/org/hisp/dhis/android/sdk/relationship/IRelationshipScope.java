package org.hisp.dhis.android.sdk.relationship;


import org.hisp.dhis.java.sdk.models.relationship.Relationship;
import org.hisp.dhis.java.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.List;

import rx.Observable;

public interface IRelationshipScope {

    Observable<Relationship> get(long id);

    Observable<List<Relationship>> list();

    Observable<List<Relationship>> list(TrackedEntityInstance object);

    Observable<Boolean> save(Relationship object);

    Observable<Boolean> remove(Relationship object);
}
