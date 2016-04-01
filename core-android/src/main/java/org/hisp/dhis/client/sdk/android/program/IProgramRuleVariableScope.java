package org.hisp.dhis.client.sdk.android.program;

import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;

import java.util.List;
import java.util.Set;

import rx.Observable;

public interface IProgramRuleVariableScope {
    Observable<ProgramRuleVariable> get(String uid);

    Observable<ProgramRuleVariable> get(long id);

    Observable<List<ProgramRuleVariable>> list();

    Observable<ProgramRuleVariable> getByName(Program program, String programRuleVariableName);

    Observable<List<ProgramRuleVariable>> sync();

    Observable<List<ProgramRuleVariable>> sync(SyncStrategy syncStrategy);

    Observable<List<ProgramRuleVariable>> sync(SyncStrategy syncStrategy, Set<String> uids);
}
