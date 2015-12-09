package org.hisp.dhis.android.sdk.program;

import org.hisp.dhis.java.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.java.sdk.models.program.Program;

import java.util.List;

import rx.Observable;

public interface IProgramScope {

    Observable<Boolean> save(Program program);

    Observable<Boolean> remove(Program program);

    Observable<Program> get(long id);

    Observable<Program> get(String uid);

    Observable<List<Program>> list();
}
