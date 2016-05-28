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

import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeValueService;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class TrackedEntityAttributeValueInteractorImpl implements TrackedEntityAttributeValueInteractor {
    private TrackedEntityAttributeValueService mTrackedEntityAttributeValueService;

    public TrackedEntityAttributeValueInteractorImpl(TrackedEntityAttributeValueService
                                                    trackedEntityAttributeValueService) {
        this.mTrackedEntityAttributeValueService = trackedEntityAttributeValueService;
    }

    @Override
    public Observable<TrackedEntityAttributeValue> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<TrackedEntityAttributeValue>() {
            @Override
            public void call(Subscriber<? super TrackedEntityAttributeValue> subscriber) {
                try {
                    TrackedEntityAttributeValue trackedEntityAttributeValue =
                            mTrackedEntityAttributeValueService.get(id);
                    subscriber.onNext(trackedEntityAttributeValue);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<TrackedEntityAttributeValue> get(final TrackedEntityInstance
                                                                   trackedEntityInstance, final
    TrackedEntityAttribute trackedEntityAttribute) {
        return Observable.create(new Observable.OnSubscribe<TrackedEntityAttributeValue>() {
            @Override
            public void call(Subscriber<? super TrackedEntityAttributeValue> subscriber) {
                try {
                    TrackedEntityAttributeValue trackedEntityAttributeValue =
                            mTrackedEntityAttributeValueService.get(trackedEntityInstance,
                                    trackedEntityAttribute);
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
                    List<TrackedEntityAttributeValue> trackedEntityAttributeValues =
                            mTrackedEntityAttributeValueService.list();
                    subscriber.onNext(trackedEntityAttributeValues);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<TrackedEntityAttributeValue>> list(final TrackedEntityInstance
                                                                          trackedEntityInstance) {
        return Observable.create(new Observable.OnSubscribe<List<TrackedEntityAttributeValue>>() {
            @Override
            public void call(Subscriber<? super List<TrackedEntityAttributeValue>> subscriber) {
                try {
                    List<TrackedEntityAttributeValue> trackedEntityAttributeValues =
                            mTrackedEntityAttributeValueService.list(trackedEntityInstance);
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
                    List<TrackedEntityAttributeValue> trackedEntityAttributeValues =
                            mTrackedEntityAttributeValueService.list(enrollment);
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
