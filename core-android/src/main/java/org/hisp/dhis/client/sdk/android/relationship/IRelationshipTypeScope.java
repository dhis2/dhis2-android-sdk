package org.hisp.dhis.client.sdk.android.relationship;


import org.hisp.dhis.client.sdk.models.relationship.RelationshipType;

import java.util.List;

import rx.Observable;

public interface IRelationshipTypeScope {
    Observable<RelationshipType> get(long id);

    Observable<RelationshipType> get(String uid);

    Observable<List<RelationshipType>> list();

    Observable<Boolean> save(RelationshipType object);

    Observable<Boolean> remove(RelationshipType object);
}
