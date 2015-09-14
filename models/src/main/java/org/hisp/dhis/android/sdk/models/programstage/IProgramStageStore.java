package org.hisp.dhis.android.sdk.models.programstage;

import org.hisp.dhis.android.sdk.models.common.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.program.Program;

import java.util.List;

public interface IProgramStageStore extends IIdentifiableObjectStore<ProgramStage> {
    List<ProgramStage> query(Program program);
}
