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


import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.program.IProgramStageController;
import org.hisp.dhis.client.sdk.core.program.IProgramStageService;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;

public class ProgramStageScope implements IProgramStageScope {
    private final IProgramStageService programStageService;
    private final IProgramStageController programStageController;

    public ProgramStageScope(IProgramStageService programStageService,
                             IProgramStageController programStageController) {
        this.programStageService = programStageService;
        this.programStageController = programStageController;
    }

    @Override
    public Observable<List<ProgramStage>> pullUpdates() {
        return pullUpdates(SyncStrategy.DEFAULT);
    }

    @Override
    public Observable<List<ProgramStage>> pullUpdates(String... programStageIds) {
        return pullUpdates(SyncStrategy.DEFAULT, programStageIds);
    }

    @Override
    public Observable<List<ProgramStage>> pullUpdates(final SyncStrategy syncStrategy) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramStage>>() {

            @Override
            public void call(Subscriber<? super List<ProgramStage>> subscriber) {
                try {
                    programStageController.pullUpdates(syncStrategy);
                    subscriber.onNext(programStageService.list());
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramStage>> pullUpdates(final SyncStrategy syncStrategy,
                                                      final String... programStageIds) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramStage>>() {

            @Override
            public void call(Subscriber<? super List<ProgramStage>> subscriber) {
                try {
                    Set<String> uids = new HashSet<>(ModelUtils.asList(programStageIds));
                    programStageController.pullUpdates(syncStrategy, uids);
                    subscriber.onNext(programStageService.list(uids));
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<ProgramStage> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<ProgramStage>() {
            @Override
            public void call(Subscriber<? super ProgramStage> subscriber) {
                try {
                    ProgramStage programStage = programStageService.get(uid);
                    subscriber.onNext(programStage);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<ProgramStage> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<ProgramStage>() {
            @Override
            public void call(Subscriber<? super ProgramStage> subscriber) {
                try {
                    ProgramStage programStage = programStageService.get(id);
                    subscriber.onNext(programStage);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramStage>> list() {
        return Observable.create(new Observable.OnSubscribe<List<ProgramStage>>() {
            @Override
            public void call(Subscriber<? super List<ProgramStage>> subscriber) {
                try {
                    List<ProgramStage> programStages = programStageService.list();
                    subscriber.onNext(programStages);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramStage>> list(final Program program) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramStage>>() {
            @Override
            public void call(Subscriber<? super List<ProgramStage>> subscriber) {
                try {
                    List<ProgramStage> programStages = programStageService.list(program);
                    subscriber.onNext(programStages);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
