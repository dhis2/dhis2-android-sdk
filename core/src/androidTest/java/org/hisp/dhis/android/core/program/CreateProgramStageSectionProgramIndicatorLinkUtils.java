package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.support.annotation.NonNull;

public class CreateProgramStageSectionProgramIndicatorLinkUtils {

    public static ContentValues create(@NonNull String programStageSectionUid, @NonNull String programIndicatorUid) {
        ContentValues values = new ContentValues();
        values.put(ProgramStageSectionProgramIndicatorLinkModel.Columns.PROGRAM_STAGE_SECTION, programStageSectionUid);
        values.put(ProgramStageSectionProgramIndicatorLinkModel.Columns.PROGRAM_INDICATOR, programIndicatorUid);
        return values;
    }
}
