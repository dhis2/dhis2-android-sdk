package org.hisp.dhis.android.sdk.trackedentity;

import org.hisp.dhis.java.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.java.sdk.trackedentity.TrackedEntityAttributeController;
import org.hisp.dhis.java.sdk.trackedentity.TrackedEntityAttributeService;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class TrackedEntityAttributeScope implements ITrackedEntityAttributeScope {
    private TrackedEntityAttributeService mTrackedEntityAttributeService;
    private TrackedEntityAttributeController mTrackedEntityAttributeController;

    public TrackedEntityAttributeScope(TrackedEntityAttributeService trackedEntityAttributeService, TrackedEntityAttributeController trackedEntityAttributeController) {
        this.mTrackedEntityAttributeService = trackedEntityAttributeService;
        this.mTrackedEntityAttributeController = trackedEntityAttributeController;
    }

    @Override
    public Observable<TrackedEntityAttribute> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<TrackedEntityAttribute>() {
            @Override
            public void call(Subscriber<? super TrackedEntityAttribute> subscriber) {
                try {
                    TrackedEntityAttribute trackedEntityAttribute = mTrackedEntityAttributeService.get(id);
                    subscriber.onNext(trackedEntityAttribute);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<TrackedEntityAttribute> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<TrackedEntityAttribute>() {
            @Override
            public void call(Subscriber<? super TrackedEntityAttribute> subscriber) {
                try {
                    TrackedEntityAttribute trackedEntityAttribute = mTrackedEntityAttributeService.get(uid);
                    subscriber.onNext(trackedEntityAttribute);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<TrackedEntityAttribute>> list() {
        return Observable.create(new Observable.OnSubscribe<List<TrackedEntityAttribute>>() {
            @Override
            public void call(Subscriber<? super List<TrackedEntityAttribute>> subscriber) {
                try {
                    List<TrackedEntityAttribute> trackedEntityAttributes = mTrackedEntityAttributeService.list();
                    subscriber.onNext(trackedEntityAttributes);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final TrackedEntityAttribute object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mTrackedEntityAttributeService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final TrackedEntityAttribute object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mTrackedEntityAttributeService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> send() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    mTrackedEntityAttributeController.sync();
//                    boolean status = mTrackedEntityAttributeController.sync();
//                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
