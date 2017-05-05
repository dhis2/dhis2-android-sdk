package org.hisp.dhis.android.core.program;

import android.support.annotation.NonNull;

public interface ProgramStageSectionDataElementLinkStore {
    Long insert(@NonNull String programStageSection, @NonNull String dataElement);

    int update(@NonNull String programStageSection, @NonNull String dataElement,
                      @NonNull String whereProgramStageSection, @NonNull String whereDataElement);
}
