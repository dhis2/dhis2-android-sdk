package org.hisp.dhis.client.sdk.android.program;

import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;

import java.util.List;

import rx.Observable;

public interface IProgramRuleScope {
    Observable<ProgramRule> get(String uid);

    Observable<ProgramRule> get(long id);

    Observable<List<ProgramRule>> list();

    Observable<List<ProgramRule>> list(ProgramStage programStage);

    Observable<List<ProgramRule>> list(Program program);

    Observable<Boolean> save(ProgramRule object);

    Observable<Boolean> remove(ProgramRule object);
}
