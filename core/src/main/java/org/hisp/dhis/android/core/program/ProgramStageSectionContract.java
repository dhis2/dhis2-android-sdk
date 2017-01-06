package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectContract;

public class ProgramStageSectionContract {
    public interface Columns extends BaseIdentifiableObjectContract.Columns {
        String SORT_ORDER = "sortOrder";
        String PROGRAM_STAGE = "programStage";
    }
}
