package org.hisp.dhis.android.sdk.trackedentity;

import org.hisp.dhis.java.sdk.event.EventController;
import org.hisp.dhis.java.sdk.models.dataelement.DataElement;
import org.hisp.dhis.java.sdk.models.event.Event;
import org.hisp.dhis.java.sdk.models.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.java.sdk.trackedentity.TrackedEntityDataValueService;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class TrackedEntityDataValueScope implements ITrackedEntityDataValueScope {
    private final TrackedEntityDataValueService mTrackedEntityDataValueService;
    private final EventController mEventController;

    public TrackedEntityDataValueScope(TrackedEntityDataValueService trackedEntityDataValueService, EventController eventController) {
        this.mTrackedEntityDataValueService = trackedEntityDataValueService;
        this.mEventController = eventController;
    }

    @Override
    public Observable<TrackedEntityDataValue> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<TrackedEntityDataValue>() {
            @Override
            public void call(Subscriber<? super TrackedEntityDataValue> subscriber) {
                try {
                    TrackedEntityDataValue trackedEntityDataValue = mTrackedEntityDataValueService.get(id);
                    subscriber.onNext(trackedEntityDataValue);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<TrackedEntityDataValue>> list() {
        return Observable.create(new Observable.OnSubscribe<List<TrackedEntityDataValue>>() {
            @Override
            public void call(Subscriber<? super List<TrackedEntityDataValue>> subscriber) {
                try {
                    List<TrackedEntityDataValue> trackedEntityDataValues = mTrackedEntityDataValueService.list();
                    subscriber.onNext(trackedEntityDataValues);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final TrackedEntityDataValue object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mTrackedEntityDataValueService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final TrackedEntityDataValue object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mTrackedEntityDataValueService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> add(final TrackedEntityDataValue object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mTrackedEntityDataValueService.add(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> update(final TrackedEntityDataValue object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mTrackedEntityDataValueService.update(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<TrackedEntityDataValue> create(final Event event, final String dataElement, final boolean providedElsewhere, final String storedBy, final String value) {
        return Observable.create(new Observable.OnSubscribe<TrackedEntityDataValue>() {
            @Override
            public void call(Subscriber<? super TrackedEntityDataValue> subscriber) {
                try {
                    TrackedEntityDataValue trackedEntityDataValue = mTrackedEntityDataValueService.create(event, dataElement, providedElsewhere, storedBy, value);
                    subscriber.onNext(trackedEntityDataValue);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<TrackedEntityDataValue>> list(final Event event) {
        return Observable.create(new Observable.OnSubscribe<List<TrackedEntityDataValue>>() {
            @Override
            public void call(Subscriber<? super List<TrackedEntityDataValue>> subscriber) {
                try {
                    List<TrackedEntityDataValue> trackedEntityDataValues = mTrackedEntityDataValueService.list(event);
                    subscriber.onNext(trackedEntityDataValues);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<TrackedEntityDataValue> get(final DataElement dataElement, final Event event) {
        return Observable.create(new Observable.OnSubscribe<TrackedEntityDataValue>() {
            @Override
            public void call(Subscriber<? super TrackedEntityDataValue> subscriber) {
                try {
                    TrackedEntityDataValue trackedEntityDataValue = mTrackedEntityDataValueService.get(dataElement, event);
                    subscriber.onNext(trackedEntityDataValue);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Void> send(final String uid) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    mEventController.sync(uid);
//                    subscriber.onNext();
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
