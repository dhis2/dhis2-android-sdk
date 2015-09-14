package org.hisp.dhis.android.sdk.models.metadata;

import org.hisp.dhis.android.sdk.models.common.IStore;

import java.util.List;

public interface IProgramTrackedEntityAttributeStore extends IStore<ProgramTrackedEntityAttribute> {
    List<ProgramTrackedEntityAttribute> query(Program program);
    ProgramTrackedEntityAttribute query(Program program, TrackedEntityAttribute trackedEntityAttribute);
}
