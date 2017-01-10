package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectContract;

public class ProgramRuleVariableContract {
    public interface Columns extends BaseIdentifiableObjectContract.Columns {
        String PROGRAM_STAGE = "programStage";
        String PROGRAM_RULE_VARIABLE_SOURCE_TYPE = "programRuleVariableSourceType";
        String USE_CODE_FOR_OPTION_SET = "useCodeForOptionSet";
        String PROGRAM = "program";
        String DATA_ELEMENT = "dataElement";
        String TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute";
    }
}
