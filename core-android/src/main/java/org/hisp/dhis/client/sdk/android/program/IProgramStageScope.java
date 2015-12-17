package org.hisp.dhis.client.sdk.android.program;

import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;

import java.util.List;

import rx.Observable;

public interface IProgramStageScope {
    Observable<ProgramStage> get(String uid);

    Observable<ProgramStage> get(long id);

    Observable<List<ProgramStage>> list();

    Observable<List<ProgramStage>> list(Program program);

    Observable<Boolean> save(ProgramStage object);

    Observable<Boolean> remove(ProgramStage object);
}
