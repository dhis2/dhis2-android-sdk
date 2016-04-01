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
import org.hisp.dhis.client.sdk.core.program.IProgramRuleController;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleService;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;

import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;

public class ProgramRuleScope implements IProgramRuleScope {
    private final IProgramRuleService programRuleService;
    private final IProgramRuleController programRuleController;

    public ProgramRuleScope(IProgramRuleService programRuleService,
                            IProgramRuleController programRuleController) {
        this.programRuleService = programRuleService;
        this.programRuleController = programRuleController;
    }

    @Override
    public Observable<ProgramRule> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<ProgramRule>() {
            @Override
            public void call(Subscriber<? super ProgramRule> subscriber) {
                try {
                    ProgramRule programRule = programRuleService.get(uid);
                    subscriber.onNext(programRule);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<ProgramRule> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<ProgramRule>() {
            @Override
            public void call(Subscriber<? super ProgramRule> subscriber) {
                try {
                    ProgramRule programRule = programRuleService.get(id);
                    subscriber.onNext(programRule);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramRule>> list() {
        return Observable.create(new Observable.OnSubscribe<List<ProgramRule>>() {
            @Override
            public void call(Subscriber<? super List<ProgramRule>> subscriber) {
                try {
                    List<ProgramRule> programRules = programRuleService.list();
                    subscriber.onNext(programRules);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramRule>> list(final ProgramStage programStage) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramRule>>() {
            @Override
            public void call(Subscriber<? super List<ProgramRule>> subscriber) {
                try {
                    List<ProgramRule> programRules = programRuleService.list(programStage);
                    subscriber.onNext(programRules);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramRule>> list(final Program program) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramRule>>() {
            @Override
            public void call(Subscriber<? super List<ProgramRule>> subscriber) {
                try {
                    List<ProgramRule> programRules = programRuleService.list(program);
                    subscriber.onNext(programRules);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final ProgramRule object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = programRuleService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final ProgramRule object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = programRuleService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramRule>> sync() {
        return sync(SyncStrategy.DEFAULT);
    }

    @Override
    public Observable<List<ProgramRule>> sync(final SyncStrategy syncStrategy) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramRule>>() {
            @Override
            public void call(Subscriber<? super List<ProgramRule>> subscriber) {
                try {
                    programRuleController.sync(syncStrategy);
                    List<ProgramRule> programRules = programRuleService.list();
                    subscriber.onNext(programRules);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramRule>> sync(final SyncStrategy syncStrategy, final Set<String> uids) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramRule>>() {
            @Override
            public void call(Subscriber<? super List<ProgramRule>> subscriber) {
                try {
                    programRuleController.sync(syncStrategy, uids);
                    List<ProgramRule> programRules = programRuleService.list();
                    subscriber.onNext(programRules);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramRule>> sync(final SyncStrategy syncStrategy, final List<Program> programs) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramRule>>() {
            @Override
            public void call(Subscriber<? super List<ProgramRule>> subscriber) {
                try {
                    programRuleController.sync(syncStrategy, programs);
                    List<ProgramRule> programRules = programRuleService.list();
                    subscriber.onNext(programRules);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }


}
