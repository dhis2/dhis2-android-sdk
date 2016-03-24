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


import org.hisp.dhis.client.sdk.core.program.IProgramIndicatorService;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramIndicator;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class ProgramIndicatorScope implements IProgramIndicatorScope {

    private IProgramIndicatorService mProgramIndicatorService;

    public ProgramIndicatorScope(IProgramIndicatorService mProgramIndicatorService) {
        this.mProgramIndicatorService = mProgramIndicatorService;
    }

    @Override
    public Observable<ProgramIndicator> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<ProgramIndicator>() {
            @Override
            public void call(Subscriber<? super ProgramIndicator> subscriber) {
                try {
                    ProgramIndicator programIndicator = mProgramIndicatorService.get(uid);
                    subscriber.onNext(programIndicator);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<ProgramIndicator> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<ProgramIndicator>() {
            @Override
            public void call(Subscriber<? super ProgramIndicator> subscriber) {
                try {
                    ProgramIndicator programIndicator = mProgramIndicatorService.get(id);
                    subscriber.onNext(programIndicator);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramIndicator>> list() {
        return Observable.create(new Observable.OnSubscribe<List<ProgramIndicator>>() {
            @Override
            public void call(Subscriber<? super List<ProgramIndicator>> subscriber) {
                try {
                    List<ProgramIndicator> programIndicators = mProgramIndicatorService.list();
                    subscriber.onNext(programIndicators);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramIndicator>> list(final Program program) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramIndicator>>() {
            @Override
            public void call(Subscriber<? super List<ProgramIndicator>> subscriber) {
                try {
                    List<ProgramIndicator> programIndicators = mProgramIndicatorService.list
                            (program);
                    subscriber.onNext(programIndicators);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramIndicator>> list(final ProgramStage programStage) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramIndicator>>() {
            @Override
            public void call(Subscriber<? super List<ProgramIndicator>> subscriber) {
                try {
                    List<ProgramIndicator> programIndicators = mProgramIndicatorService.list
                            (programStage);
                    subscriber.onNext(programIndicators);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramIndicator>> list(final ProgramStageSection programStageSection) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramIndicator>>() {
            @Override
            public void call(Subscriber<? super List<ProgramIndicator>> subscriber) {
                try {
                    List<ProgramIndicator> programIndicators = mProgramIndicatorService.list
                            (programStageSection);
                    subscriber.onNext(programIndicators);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final ProgramIndicator object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mProgramIndicatorService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final ProgramIndicator object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mProgramIndicatorService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
