package org.hisp.dhis.client.sdk.android.program;


import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;

import java.util.List;

import rx.Observable;

public interface IProgramRuleActionScope {
    Observable<ProgramRuleAction> get(String uid);

    Observable<ProgramRuleAction> get(long id);

    Observable<List<ProgramRuleAction>> list();

    Observable<List<ProgramRuleAction>> list(ProgramRule programRule);

    Observable<Boolean> save(ProgramRuleAction object);

    Observable<Boolean> remove(ProgramRuleAction object);
}
