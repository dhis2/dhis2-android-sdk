package org.hisp.dhis.android.sdk.models.metadata;

import org.hisp.dhis.android.sdk.models.common.IStore;

import java.util.List;

public interface IProgramStageDataElementStore extends IStore<ProgramStageDataElement> {
    List<ProgramStageDataElement> query(ProgramStage programStage);
    List<ProgramStageDataElement> query(ProgramStageSection programStageSection);
    ProgramStageDataElement query(ProgramStage programStage, DataElement dataElement);
}
