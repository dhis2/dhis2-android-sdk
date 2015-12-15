package org.hisp.dhis.android.sdk.relationship;

import org.hisp.dhis.java.sdk.models.relationship.Relationship;
import org.hisp.dhis.java.sdk.models.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.java.sdk.relationship.RelationshipService;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class RelationshipScope implements IRelationshipScope {
    private RelationshipService mRelationshipService;

    public RelationshipScope(RelationshipService relationshipService) {
        this.mRelationshipService = relationshipService;
    }

    @Override
    public Observable<Relationship> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<Relationship>() {
            @Override
            public void call(Subscriber<? super Relationship> subscriber) {
                try {
                    Relationship relationship = mRelationshipService.get(id);
                    subscriber.onNext(relationship);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<Relationship>> list() {
        return Observable.create(new Observable.OnSubscribe<List<Relationship>>() {
            @Override
            public void call(Subscriber<? super List<Relationship>> subscriber) {
                try {
                    List<Relationship> relationships = mRelationshipService.list();
                    subscriber.onNext(relationships);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<Relationship>> list(final TrackedEntityInstance object) {
        return Observable.create(new Observable.OnSubscribe<List<Relationship>>() {
            @Override
            public void call(Subscriber<? super List<Relationship>> subscriber) {
                try {
                    List<Relationship> relationships = mRelationshipService.list(object);
                    subscriber.onNext(relationships);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final Relationship object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mRelationshipService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final Relationship object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mRelationshipService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
