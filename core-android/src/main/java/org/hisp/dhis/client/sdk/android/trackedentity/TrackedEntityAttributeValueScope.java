package org.hisp.dhis.client.sdk.android.trackedentity;

import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeValueService;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class TrackedEntityAttributeValueScope implements ITrackedEntityAttributeValueScope {
    private TrackedEntityAttributeValueService mTrackedEntityAttributeValueService;

    public TrackedEntityAttributeValueScope(TrackedEntityAttributeValueService trackedEntityAttributeValueService) {
        this.mTrackedEntityAttributeValueService = trackedEntityAttributeValueService;
    }

    @Override
    public Observable<TrackedEntityAttributeValue> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<TrackedEntityAttributeValue>() {
            @Override
            public void call(Subscriber<? super TrackedEntityAttributeValue> subscriber) {
                try {
                    TrackedEntityAttributeValue trackedEntityAttributeValue = mTrackedEntityAttributeValueService.get(id);
                    subscriber.onNext(trackedEntityAttributeValue);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<TrackedEntityAttributeValue> get(final TrackedEntityInstance trackedEntityInstance, final TrackedEntityAttribute trackedEntityAttribute) {
        return Observable.create(new Observable.OnSubscribe<TrackedEntityAttributeValue>() {
            @Override
            public void call(Subscriber<? super TrackedEntityAttributeValue> subscriber) {
                try {
                    TrackedEntityAttributeValue trackedEntityAttributeValue = mTrackedEntityAttributeValueService.get(trackedEntityInstance, trackedEntityAttribute);
                    subscriber.onNext(trackedEntityAttributeValue);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<TrackedEntityAttributeValue>> list() {
        return Observable.create(new Observable.OnSubscribe<List<TrackedEntityAttributeValue>>() {
            @Override
            public void call(Subscriber<? super List<TrackedEntityAttributeValue>> subscriber) {
                try {
                    List<TrackedEntityAttributeValue> trackedEntityAttributeValues = mTrackedEntityAttributeValueService.list();
                    subscriber.onNext(trackedEntityAttributeValues);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<TrackedEntityAttributeValue>> list(final TrackedEntityInstance trackedEntityInstance) {
        return Observable.create(new Observable.OnSubscribe<List<TrackedEntityAttributeValue>>() {
            @Override
            public void call(Subscriber<? super List<TrackedEntityAttributeValue>> subscriber) {
                try {
                    List<TrackedEntityAttributeValue> trackedEntityAttributeValues = mTrackedEntityAttributeValueService.list(trackedEntityInstance);
                    subscriber.onNext(trackedEntityAttributeValues);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<TrackedEntityAttributeValue>> list(final Enrollment enrollment) {
        return Observable.create(new Observable.OnSubscribe<List<TrackedEntityAttributeValue>>() {
            @Override
            public void call(Subscriber<? super List<TrackedEntityAttributeValue>> subscriber) {
                try {
                    List<TrackedEntityAttributeValue> trackedEntityAttributeValues = mTrackedEntityAttributeValueService.list(enrollment);
                    subscriber.onNext(trackedEntityAttributeValues);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final TrackedEntityAttributeValue object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mTrackedEntityAttributeValueService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final TrackedEntityAttributeValue object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mTrackedEntityAttributeValueService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
