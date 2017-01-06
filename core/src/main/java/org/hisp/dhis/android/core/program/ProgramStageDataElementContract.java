package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectContract;

public class ProgramStageDataElementContract {
    public interface Columns extends BaseIdentifiableObjectContract.Columns {
        String DISPLAY_IN_REPORTS = "displayInReports";
        String COMPULSORY = "compulsory";
        String ALLOW_PROVIDED_ELSEWHERE = "allowProvidedElsewhere";
        String SORT_ORDER = "sortOrder";
        String ALLOW_FUTURE_DATE = "allowFutureDate";
        String DATA_ELEMENT = "dataElement";
        String PROGRAM_STAGE_SECTION = "programStageSection";
    }
}
