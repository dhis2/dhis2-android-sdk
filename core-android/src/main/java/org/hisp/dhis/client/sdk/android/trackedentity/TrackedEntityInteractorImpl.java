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

import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityService;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class TrackedEntityInteractorImpl implements TrackedEntityInteractor {
    private TrackedEntityService mTrackedEntityService;

    public TrackedEntityInteractorImpl(TrackedEntityService trackedEntityService) {
        this.mTrackedEntityService = trackedEntityService;
    }

    @Override
    public Observable<TrackedEntity> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<TrackedEntity>() {
            @Override
            public void call(Subscriber<? super TrackedEntity> subscriber) {
                try {
                    TrackedEntity trackedEntity = mTrackedEntityService.get(id);
                    subscriber.onNext(trackedEntity);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<TrackedEntity> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<TrackedEntity>() {
            @Override
            public void call(Subscriber<? super TrackedEntity> subscriber) {
                try {
                    TrackedEntity trackedEntity = mTrackedEntityService.get(uid);
                    subscriber.onNext(trackedEntity);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<TrackedEntity>> list() {
        return Observable.create(new Observable.OnSubscribe<List<TrackedEntity>>() {
            @Override
            public void call(Subscriber<? super List<TrackedEntity>> subscriber) {
                try {
                    List<TrackedEntity> trackedEntities = mTrackedEntityService.list();
                    subscriber.onNext(trackedEntities);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final TrackedEntity object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mTrackedEntityService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final TrackedEntity object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mTrackedEntityService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
