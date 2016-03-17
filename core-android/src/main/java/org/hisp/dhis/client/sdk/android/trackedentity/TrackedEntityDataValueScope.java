/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.android.trackedentity;

import org.hisp.dhis.client.sdk.core.event.IEventController;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityDataValueService;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class TrackedEntityDataValueScope implements ITrackedEntityDataValueScope {
    private final ITrackedEntityDataValueService mTrackedEntityDataValueService;
    private final IEventController mEventController;

    public TrackedEntityDataValueScope(ITrackedEntityDataValueService trackedEntityDataValueService, IEventController eventController) {
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
                    mEventController.sync(Arrays.asList(uid));
//                    subscriber.onNext();
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
