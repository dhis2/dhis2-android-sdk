package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import org.hisp.dhis.android.core.program.ProgramStageDataElementModel.Columns;
public class CreateProgramStageDataElementUtils {
 
    public static ContentValues create(Long id, String uid, String programStageUid, String dataElementUid) {
        ContentValues programStageDataElement = new ContentValues();
        programStageDataElement.put(Columns.ID, id);
        programStageDataElement.put(Columns.UID, uid);
        programStageDataElement.put(Columns.DISPLAY_NAME, "test_displayName");
        programStageDataElement.put(Columns.COMPULSORY, Boolean.TRUE);
        programStageDataElement.put(Columns.DISPLAY_IN_REPORTS, Boolean.TRUE);
        programStageDataElement.put(Columns.DATA_ELEMENT, dataElementUid);
        programStageDataElement.put(Columns.PROGRAM_STAGE, programStageUid);
        return programStageDataElement;
    }
}
