package org.hisp.dhis.android.core.option;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectContract;

public class OptionSetContract {

    public static final String OPTION_SETS = "optionSets";
    public static final String OPTION_SETS_ID = OPTION_SETS + "/#";


    public interface Columns extends BaseIdentifiableObjectContract.Columns {
        String VERSION = "version";
        String VALUE_TYPE = "valueType";
    }
}
