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

package org.hisp.dhis.client.sdk.android.program;


import org.hisp.dhis.client.sdk.core.program.IProgramTrackedEntityAttributeService;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramTrackedEntityAttribute;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class ProgramTrackedEntityAttributeScope implements IProgramTrackedEntityAttributeScope {
    private IProgramTrackedEntityAttributeService mProgramTrackedEntityAttributeService;

    public ProgramTrackedEntityAttributeScope(IProgramTrackedEntityAttributeService programTrackedEntityAttributeService) {
        mProgramTrackedEntityAttributeService = programTrackedEntityAttributeService;
    }

    @Override
    public Observable<ProgramTrackedEntityAttribute> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<ProgramTrackedEntityAttribute>() {
            @Override
            public void call(Subscriber<? super ProgramTrackedEntityAttribute> subscriber) {
                try {
                    ProgramTrackedEntityAttribute programTrackedEntityAttribute = mProgramTrackedEntityAttributeService.get(id);
                    subscriber.onNext(programTrackedEntityAttribute);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramTrackedEntityAttribute>> list() {
        return Observable.create(new Observable.OnSubscribe<List<ProgramTrackedEntityAttribute>>() {
            @Override
            public void call(Subscriber<? super List<ProgramTrackedEntityAttribute>> subscriber) {
                try {
                    List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = mProgramTrackedEntityAttributeService.list();
                    subscriber.onNext(programTrackedEntityAttributes);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramTrackedEntityAttribute>> list(final Program program) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramTrackedEntityAttribute>>() {
            @Override
            public void call(Subscriber<? super List<ProgramTrackedEntityAttribute>> subscriber) {
                try {
                    List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = mProgramTrackedEntityAttributeService.list(program);
                    subscriber.onNext(programTrackedEntityAttributes);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final ProgramTrackedEntityAttribute object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mProgramTrackedEntityAttributeService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final ProgramTrackedEntityAttribute object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mProgramTrackedEntityAttributeService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
