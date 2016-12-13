package org.hisp.dhis.android.core.common;

public final class BaseNameableObjectContract {
    public interface Columns extends BaseIdentifiableObjectContract.Columns {
        String SHORT_NAME = "shortName";
        String DISPLAY_SHORT_NAME = "displayShortName";
        String DESCRIPTION = "description";
        String DISPLAY_DESCRIPTION = "displayDescription";
    }
}
