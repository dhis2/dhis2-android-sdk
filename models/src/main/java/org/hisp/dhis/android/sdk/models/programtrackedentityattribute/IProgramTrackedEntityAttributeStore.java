package org.hisp.dhis.android.sdk.models.programtrackedentityattribute;

import org.hisp.dhis.android.sdk.models.common.IStore;
import org.hisp.dhis.android.sdk.models.trackedentityattribute.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.models.program.Program;

import java.util.List;

public interface IProgramTrackedEntityAttributeStore extends IStore<ProgramTrackedEntityAttribute> {
    List<ProgramTrackedEntityAttribute> query(Program program);
    ProgramTrackedEntityAttribute query(Program program, TrackedEntityAttribute trackedEntityAttribute);
}
