package org.hisp.dhis.android.sdk.program;


import org.hisp.dhis.java.sdk.models.program.Program;
import org.hisp.dhis.java.sdk.models.program.ProgramIndicator;

import java.util.List;

import rx.Observable;

public interface IProgramIndicatorScope {
    Observable<ProgramIndicator> get(String uid);

    Observable<ProgramIndicator> get(long id);

    Observable<List<ProgramIndicator>> list();

    Observable<List<ProgramIndicator>> getProgramIndicators(Program program);

    Observable<Boolean> save(ProgramIndicator object);

    Observable<Boolean> remove(ProgramIndicator object);
}
