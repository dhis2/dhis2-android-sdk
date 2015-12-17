package org.hisp.dhis.client.sdk.android.relationship;


import org.hisp.dhis.client.sdk.core.relationship.RelationshipTypeService;
import org.hisp.dhis.client.sdk.models.relationship.RelationshipType;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class RelationshipTypeScope implements IRelationshipTypeScope {

    private RelationshipTypeService mRelationshipTypeService;

    public RelationshipTypeScope(RelationshipTypeService relationshipTypeService) {
        this.mRelationshipTypeService = relationshipTypeService;
    }

    @Override
    public Observable<RelationshipType> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<RelationshipType>() {
            @Override
            public void call(Subscriber<? super RelationshipType> subscriber) {
                try {
                    RelationshipType relationshipType = mRelationshipTypeService.get(id);
                    subscriber.onNext(relationshipType);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<RelationshipType> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<RelationshipType>() {
            @Override
            public void call(Subscriber<? super RelationshipType> subscriber) {
                try {
                    RelationshipType relationshipType = mRelationshipTypeService.get(uid);
                    subscriber.onNext(relationshipType);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<RelationshipType>> list() {
        return Observable.create(new Observable.OnSubscribe<List<RelationshipType>>() {
            @Override
            public void call(Subscriber<? super List<RelationshipType>> subscriber) {
                try {
                    List<RelationshipType> relationshipTypes = mRelationshipTypeService.list();
                    subscriber.onNext(relationshipTypes);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final RelationshipType object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mRelationshipTypeService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final RelationshipType object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mRelationshipTypeService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
