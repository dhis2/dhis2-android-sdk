package org.hisp.dhis.android.sdk.program;

import org.hisp.dhis.java.sdk.models.program.ProgramRule;
import org.hisp.dhis.java.sdk.models.program.ProgramRuleAction;
import org.hisp.dhis.java.sdk.program.ProgramRuleActionService;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class ProgramRuleActionScope implements IProgramRuleActionScope {
    private ProgramRuleActionService mProgramRuleActionService;

    public ProgramRuleActionScope(ProgramRuleActionService programRuleActionService) {
        this.mProgramRuleActionService = programRuleActionService;
    }

    @Override
    public Observable<ProgramRuleAction> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<ProgramRuleAction>() {
            @Override
            public void call(Subscriber<? super ProgramRuleAction> subscriber) {
                try {
                    ProgramRuleAction programRuleAction = mProgramRuleActionService.get(uid);
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
                    ProgramRuleAction programRuleAction = mProgramRuleActionService.get(id);
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
                    List<ProgramRuleAction> programRuleActions = mProgramRuleActionService.list();
                    subscriber.onNext(programRuleActions);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramRuleAction>> getProgramRuleActions(final ProgramRule programRule) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramRuleAction>>() {
            @Override
            public void call(Subscriber<? super List<ProgramRuleAction>> subscriber) {
                try {
                    List<ProgramRuleAction> programRuleActions = mProgramRuleActionService.query(programRule);
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
                    boolean status = mProgramRuleActionService.save(object);
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
                    boolean status = mProgramRuleActionService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
