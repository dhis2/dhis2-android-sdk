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
import org.hisp.dhis.client.sdk.core.program.IProgramRuleActionController;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleActionService;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;

import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;

public class ProgramRuleActionScope implements IProgramRuleActionScope {
    private final IProgramRuleActionService programRuleActionService;
    private final IProgramRuleActionController programRuleActionController;

    public ProgramRuleActionScope(IProgramRuleActionService programRuleActionService,
                                  IProgramRuleActionController programRuleActionController) {
        this.programRuleActionService = programRuleActionService;
        this.programRuleActionController = programRuleActionController;
    }

    @Override
    public Observable<ProgramRuleAction> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<ProgramRuleAction>() {
            @Override
            public void call(Subscriber<? super ProgramRuleAction> subscriber) {
                try {
                    ProgramRuleAction programRuleAction = programRuleActionService.get(uid);
                    subscriber.onNext(programRuleAction);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<ProgramRuleAction> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<ProgramRuleAction>() {
            @Override
            public void call(Subscriber<? super ProgramRuleAction> subscriber) {
                try {
                    ProgramRuleAction programRuleAction = programRuleActionService.get(id);
                    subscriber.onNext(programRuleAction);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramRuleAction>> list() {
        return Observable.create(new Observable.OnSubscribe<List<ProgramRuleAction>>() {
            @Override
            public void call(Subscriber<? super List<ProgramRuleAction>> subscriber) {
                try {
                    List<ProgramRuleAction> programRuleActions = programRuleActionService.list();
                    subscriber.onNext(programRuleActions);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramRuleAction>> list(final ProgramRule programRule) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramRuleAction>>() {
            @Override
            public void call(Subscriber<? super List<ProgramRuleAction>> subscriber) {
                try {
                    List<ProgramRuleAction> programRuleActions = programRuleActionService.list
                            (programRule);
                    subscriber.onNext(programRuleActions);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final ProgramRuleAction object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = programRuleActionService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final ProgramRuleAction object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = programRuleActionService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramRuleAction>> sync() {
        return sync(SyncStrategy.DEFAULT);
    }

    @Override
    public Observable<List<ProgramRuleAction>> sync(final SyncStrategy syncStrategy) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramRuleAction>>() {
            @Override
            public void call(Subscriber<? super List<ProgramRuleAction>> subscriber) {
                try {
                    programRuleActionController.sync(syncStrategy);
                    List<ProgramRuleAction> programRulesActions = programRuleActionService.list();
                    subscriber.onNext(programRulesActions);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramRuleAction>> sync(final SyncStrategy syncStrategy, final Set<String> uids) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramRuleAction>>() {
            @Override
            public void call(Subscriber<? super List<ProgramRuleAction>> subscriber) {
                try {
                    programRuleActionController.sync(syncStrategy, uids);
                    List<ProgramRuleAction> programRulesActions = programRuleActionService.list();
                    subscriber.onNext(programRulesActions);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
