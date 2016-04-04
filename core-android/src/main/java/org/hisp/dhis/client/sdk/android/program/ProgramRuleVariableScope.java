package org.hisp.dhis.client.sdk.android.program;

import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleVariableController;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleVariableService;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;

import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;

public class ProgramRuleVariableScope implements IProgramRuleVariableScope {
    private final IProgramRuleVariableService programRuleVariableService;
    private final IProgramRuleVariableController programRuleVariableController;

    public ProgramRuleVariableScope(IProgramRuleVariableService programRuleVariableService,
                                    IProgramRuleVariableController programRuleVariableController) {
        this.programRuleVariableService = programRuleVariableService;
        this.programRuleVariableController = programRuleVariableController;
    }

    @Override
    public Observable<ProgramRuleVariable> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<ProgramRuleVariable>() {
            @Override
            public void call(Subscriber<? super ProgramRuleVariable> subscriber) {
                try {
                    ProgramRuleVariable programRuleVariable = programRuleVariableService.get(uid);
                    subscriber.onNext(programRuleVariable);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<ProgramRuleVariable> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<ProgramRuleVariable>() {
            @Override
            public void call(Subscriber<? super ProgramRuleVariable> subscriber) {
                try {
                    ProgramRuleVariable programRuleVariable = programRuleVariableService.get(id);
                    subscriber.onNext(programRuleVariable);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramRuleVariable>> list() {
        return Observable.create(new Observable.OnSubscribe<List<ProgramRuleVariable>>() {
            @Override
            public void call(Subscriber<? super List<ProgramRuleVariable>> subscriber) {
                try {
                    List<ProgramRuleVariable> programRuleVariables = programRuleVariableService.list();
                    subscriber.onNext(programRuleVariables);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<ProgramRuleVariable> get(final Program program, final String
            programRuleVariableName) {
        return Observable.create(new Observable.OnSubscribe<ProgramRuleVariable>() {
            @Override
            public void call(Subscriber<? super ProgramRuleVariable> subscriber) {
                try {
                    ProgramRuleVariable programRuleVariable = programRuleVariableService.get(program, programRuleVariableName);
                    subscriber.onNext(programRuleVariable);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramRuleVariable>> pullUpdates() {
        return pullUpdates(SyncStrategy.DEFAULT);
    }

    @Override
    public Observable<List<ProgramRuleVariable>> pullUpdates(final SyncStrategy syncStrategy) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramRuleVariable>>() {
            @Override
            public void call(Subscriber<? super List<ProgramRuleVariable>> subscriber) {
                try {
                    programRuleVariableController.pull(syncStrategy);
                    List<ProgramRuleVariable> programRuleVariables = programRuleVariableService.list();
                    subscriber.onNext(programRuleVariables);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramRuleVariable>> pullUpdates(final SyncStrategy syncStrategy,
                                                             final Set<String> uids) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramRuleVariable>>() {
            @Override
            public void call(Subscriber<? super List<ProgramRuleVariable>> subscriber) {
                try {
                    programRuleVariableController.pull(syncStrategy, uids);
                    List<ProgramRuleVariable> programRuleVariables = programRuleVariableService.list();
                    subscriber.onNext(programRuleVariables);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
