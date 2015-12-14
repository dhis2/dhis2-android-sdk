package org.hisp.dhis.android.sdk.program;


import org.hisp.dhis.java.sdk.models.program.ProgramRule;
import org.hisp.dhis.java.sdk.models.program.ProgramRuleAction;

import java.util.List;

import rx.Observable;

public interface IProgramRuleActionScope {
    Observable<ProgramRuleAction> get(String uid);

    Observable<ProgramRuleAction> get(long id);

    Observable<List<ProgramRuleAction>> list();

    Observable<List<ProgramRuleAction>> getProgramRuleActions(ProgramRule programRule);

    Observable<Boolean> save(ProgramRuleAction object);

    Observable<Boolean> remove(ProgramRuleAction object);
}
