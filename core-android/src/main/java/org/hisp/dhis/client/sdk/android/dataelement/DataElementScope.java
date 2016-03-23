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

package org.hisp.dhis.client.sdk.android.dataelement;


import org.hisp.dhis.client.sdk.core.common.controllers.IIdentifiableController;
import org.hisp.dhis.client.sdk.core.dataelement.IDataElementService;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class DataElementScope implements IDataElementScope {
    private final IDataElementService dataElementService;
    private final IIdentifiableController<DataElement> dataElementController;
    public DataElementScope(IDataElementService dataElementService,
                            IIdentifiableController<DataElement> dataElementController) {
        this.dataElementService = dataElementService;
        this.dataElementController = dataElementController;
    }

    @Override
    public Observable<DataElement> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<DataElement>() {
            @Override
            public void call(Subscriber subscriber) {
                try {
                    DataElement dataElement = dataElementService.get(uid);
                    subscriber.onNext(dataElement);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<DataElement> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<DataElement>() {
            @Override
            public void call(Subscriber subscriber) {
                try {
                    DataElement dataElement = dataElementService.get(id);
                    subscriber.onNext(dataElement);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<DataElement>> list() {
        return Observable.create(new Observable.OnSubscribe<List<DataElement>>() {
            @Override
            public void call(Subscriber<? super List<DataElement>> subscriber) {
                try {
                    List<DataElement> dataElements = dataElementService.list();
                    subscriber.onNext(dataElements);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final DataElement object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = dataElementService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final DataElement object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = dataElementService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<DataElement>> sync() {
        return Observable.create(new Observable.OnSubscribe<List<DataElement>>() {
            @Override
            public void call(Subscriber<? super List<DataElement>> subscriber) {
                try {
                    dataElementController.sync();
                    List<DataElement> dataElements = dataElementService.list();
                    subscriber.onNext(dataElements);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
