package org.hisp.dhis.android.sdk.models.metadata;

import org.hisp.dhis.android.sdk.models.common.IStore;

import java.util.List;

public interface IProgramTrackedEntityAttribute extends IStore<ProgramTrackedEntityAttribute> {
    List<ProgramTrackedEntityAttribute> query(Program program);
}
