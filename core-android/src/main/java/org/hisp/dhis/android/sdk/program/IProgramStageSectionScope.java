package org.hisp.dhis.android.sdk.program;

import org.hisp.dhis.java.sdk.models.program.ProgramStage;
import org.hisp.dhis.java.sdk.models.program.ProgramStageSection;

import java.util.List;

import rx.Observable;

public interface IProgramStageSectionScope {
    Observable<ProgramStageSection> get(String uid);

    Observable<ProgramStageSection> get(long id);

    Observable<List<ProgramStageSection>> list();

    Observable<List<ProgramStageSection>> list(ProgramStage programStage);

    Observable<Boolean> save(ProgramStageSection object);

    Observable<Boolean> remove(ProgramStageSection object);
}
