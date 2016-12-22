package org.hisp.dhis.android.core.option;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectContract;

public class OptionSetContract {

    public interface Columns extends BaseIdentifiableObjectContract.Columns {
        String VERSION = "version";
        String VALUE_TYPE = "valueType";
    }
}
