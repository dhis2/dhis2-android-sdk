package org.hisp.dhis.android.core.dataelement;

import org.hisp.dhis.android.core.common.BaseNameableObjectContract;

public class DataElementContract {
    public interface Columns extends BaseNameableObjectContract.Columns {
        String VALUE_TYPE = "valueType";
        String ZERO_IS_SIGNIFICANT = "zeroIsSignificant";
        String AGGREGATION_OPERATOR = "aggregationOperator";
        String FORM_NAME = "formName";
        String NUMBER_TYPE = "numberType";
        String DOMAIN_TYPE = "domainType";
        String DIMENSION = "dimension";
        String DISPLAY_FORM_NAME = "displayFormName";
        String OPTION_SET = "optionSet";
    }
}
