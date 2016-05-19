package org.hisp.dhis.client.sdk.android.program;

import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;

import java.util.List;
import java.util.Set;

import rx.Observable;

public interface ProgramRuleVariableInteractor {
    Observable<ProgramRuleVariable> get(String uid);

    Observable<ProgramRuleVariable> get(long id);

    Observable<List<ProgramRuleVariable>> list();

    Observable<List<ProgramRuleVariable>> pull();

    Observable<List<ProgramRuleVariable>> pull(Set<String> uids);

    Observable<List<ProgramRuleVariable>> pull(List<Program> programs);

    Observable<List<ProgramRuleVariable>> pull(SyncStrategy strategy, List<Program> programs);

    Observable<List<ProgramRuleVariable>> pull(SyncStrategy syncStrategy);

    Observable<List<ProgramRuleVariable>> pull(SyncStrategy syncStrategy, Set<String> uids);
}
