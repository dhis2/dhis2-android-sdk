package org.hisp.dhis.android.sdk.program;

import org.hisp.dhis.java.sdk.models.program.Program;
import org.hisp.dhis.java.sdk.models.program.ProgramTrackedEntityAttribute;

import java.util.List;

import rx.Observable;

public interface IProgramTrackedEntityAttributeScope {

    Observable<ProgramTrackedEntityAttribute> get(long id);

    Observable<List<ProgramTrackedEntityAttribute>> list();

    Observable<List<ProgramTrackedEntityAttribute>> list(Program program);

    Observable<Boolean> save(ProgramTrackedEntityAttribute object);

    Observable<Boolean> remove(ProgramTrackedEntityAttribute object);
}
