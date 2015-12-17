package org.hisp.dhis.client.sdk.android.program;


import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramIndicator;

import java.util.List;

import rx.Observable;

public interface IProgramIndicatorScope {
    Observable<ProgramIndicator> get(String uid);

    Observable<ProgramIndicator> get(long id);

    Observable<List<ProgramIndicator>> list();

    Observable<List<ProgramIndicator>> list(Program program);

    Observable<Boolean> save(ProgramIndicator object);

    Observable<Boolean> remove(ProgramIndicator object);
}
