package org.hisp.dhis.client.sdk.android.program;

import org.hisp.dhis.client.sdk.core.program.IProgramRuleService;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class ProgramRuleScope implements IProgramRuleScope {
    private final IProgramRuleService programRuleService;

    public ProgramRuleScope(IProgramRuleService programRuleService) {
        this.programRuleService = programRuleService;
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


}
