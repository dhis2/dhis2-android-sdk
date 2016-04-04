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


import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityAttributeController;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityAttributeService;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;

import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;

public class TrackedEntityAttributeScope implements ITrackedEntityAttributeScope {
    private ITrackedEntityAttributeService trackedEntityAttributeService;
    private ITrackedEntityAttributeController trackedEntityAttributeController;

    public TrackedEntityAttributeScope(ITrackedEntityAttributeService trackedEntityAttributeService,
                                       ITrackedEntityAttributeController
                                               trackedEntityAttributeController) {
        this.trackedEntityAttributeService = trackedEntityAttributeService;
        this.trackedEntityAttributeController = trackedEntityAttributeController;
    }

    @Override
    public Observable<TrackedEntityAttribute> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<TrackedEntityAttribute>() {
            @Override
            public void call(Subscriber<? super TrackedEntityAttribute> subscriber) {
                try {
                    TrackedEntityAttribute trackedEntityAttribute =
                            trackedEntityAttributeService.get(id);
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
                    TrackedEntityAttribute trackedEntityAttribute =
                            trackedEntityAttributeService.get(uid);
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
                    List<TrackedEntityAttribute> trackedEntityAttributes =
                            trackedEntityAttributeService.list();
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
                    boolean status = trackedEntityAttributeService.save(object);
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
                    boolean status = trackedEntityAttributeService.remove(object);
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
                    trackedEntityAttributeController.pull(SyncStrategy.DEFAULT);
//                    boolean status = trackedEntityAttributeController.pull();
//                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<TrackedEntityAttribute>> sync() {
        return sync(SyncStrategy.DEFAULT);
    }

    @Override
    public Observable<List<TrackedEntityAttribute>> sync(final SyncStrategy syncStrategy) {
        return Observable.create(new Observable.OnSubscribe<List<TrackedEntityAttribute>>() {
            @Override
            public void call(Subscriber<? super List<TrackedEntityAttribute>> subscriber) {
                try {
                    trackedEntityAttributeController.pull(syncStrategy);
                    List<TrackedEntityAttribute> trackedEntityAttributes = trackedEntityAttributeService.list();
                    subscriber.onNext(trackedEntityAttributes);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<TrackedEntityAttribute>> sync(final SyncStrategy syncStrategy, final Set<String> uids) {
        return Observable.create(new Observable.OnSubscribe<List<TrackedEntityAttribute>>() {
            @Override
            public void call(Subscriber<? super List<TrackedEntityAttribute>> subscriber) {
                try {
                    trackedEntityAttributeController.pull(syncStrategy, uids);
                    List<TrackedEntityAttribute> trackedEntityAttributes = trackedEntityAttributeService.list();
                    subscriber.onNext(trackedEntityAttributes);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });    }
}
