package org.hisp.dhis.client.sdk.android.program;

import org.hisp.dhis.client.sdk.models.program.Program;

import java.util.List;

import rx.Observable;

public interface IProgramScope {

    Observable<Boolean> save(Program program);

    Observable<Boolean> remove(Program program);

    Observable<Program> get(long id);

    Observable<Program> get(String uid);

    Observable<List<Program>> list();
}
