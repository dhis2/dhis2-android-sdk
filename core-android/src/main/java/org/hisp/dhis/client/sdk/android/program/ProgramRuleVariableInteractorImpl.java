package org.hisp.dhis.client.sdk.android.program;

import org.hisp.dhis.client.sdk.android.api.utils.DefaultOnSubscribe;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleVariableController;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleVariableService;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;

import java.util.List;
import java.util.Set;

import rx.Observable;

public class ProgramRuleVariableInteractorImpl implements ProgramRuleVariableInteractor {
    private final ProgramRuleVariableService programRuleVariableService;
    private final ProgramRuleVariableController programRuleVariableController;

    public ProgramRuleVariableInteractorImpl(ProgramRuleVariableService programRuleVariableService,
                                             ProgramRuleVariableController variableController) {
        this.programRuleVariableService = programRuleVariableService;
        this.programRuleVariableController = variableController;
    }

    @Override
    public Observable<ProgramRuleVariable> get(final String uid) {
        return Observable.create(new DefaultOnSubscribe<ProgramRuleVariable>() {
            @Override
            public ProgramRuleVariable call() {
                return programRuleVariableService.get(uid);
            }
        });
    }

    @Override
    public Observable<ProgramRuleVariable> get(final long id) {
        return Observable.create(new DefaultOnSubscribe<ProgramRuleVariable>() {
            @Override
            public ProgramRuleVariable call() {
                return programRuleVariableService.get(id);
            }
        });
    }

    @Override
    public Observable<List<ProgramRuleVariable>> list() {
        return Observable.create(new DefaultOnSubscribe<List<ProgramRuleVariable>>() {
            @Override
            public List<ProgramRuleVariable> call() {
                return programRuleVariableService.list();
            }
        });
    }

    @Override
    public Observable<List<ProgramRuleVariable>> pull() {
        return pull(SyncStrategy.DEFAULT);
    }

    @Override
    public Observable<List<ProgramRuleVariable>> pull(Set<String> uids) {
        return pull(SyncStrategy.DEFAULT, uids);
    }

    @Override
    public Observable<List<ProgramRuleVariable>> pull(final SyncStrategy syncStrategy) {
        return Observable.create(new DefaultOnSubscribe<List<ProgramRuleVariable>>() {
            @Override
            public List<ProgramRuleVariable> call() {
                programRuleVariableController.pull(syncStrategy);
                return programRuleVariableService.list();
            }
        });
    }

    @Override
    public Observable<List<ProgramRuleVariable>> pull(
            final SyncStrategy syncStrategy, final Set<String> uids) {
        return Observable.create(new DefaultOnSubscribe<List<ProgramRuleVariable>>() {
            @Override
            public List<ProgramRuleVariable> call() {
                programRuleVariableController.pull(syncStrategy, uids);
                return programRuleVariableService.list(uids);
            }
        });
    }
}
