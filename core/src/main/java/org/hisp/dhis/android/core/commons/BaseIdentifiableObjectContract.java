package org.hisp.dhis.android.core.commons;

public final class BaseIdentifiableObjectContract {
    public interface Columns extends BaseModelContract.Columns {
        String UID = "uid";
        String CODE = "code";
        String NAME = "name";
        String DISPLAY_NAME = "displayName";
        String CREATED = "created";
        String LAST_UPDATED = "lastUpdated";
    }
}
