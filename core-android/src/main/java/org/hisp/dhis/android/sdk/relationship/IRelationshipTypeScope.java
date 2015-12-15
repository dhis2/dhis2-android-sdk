package org.hisp.dhis.android.sdk.relationship;

import org.hisp.dhis.java.sdk.models.relationship.RelationshipType;

import java.util.List;

import rx.Observable;

public interface IRelationshipTypeScope {
    Observable<RelationshipType> get(long id);

    Observable<RelationshipType> get(String uid);

    Observable<List<RelationshipType>> list();

    Observable<Boolean> save(RelationshipType object);

    Observable<Boolean> remove(RelationshipType object);
}
