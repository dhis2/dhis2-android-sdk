package org.hisp.dhis.android.core.option;

import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectContract;
import org.hisp.dhis.android.core.data.database.DbContract;
import org.hisp.dhis.android.core.data.database.DbUtils;

public class OptionSetContract {

    public static final String OPTION_SETS = "optionSets";
    public static final String OPTION_SETS_ID = OPTION_SETS + "/#";

    public static final String CONTENT_TYPE_DIR = DbUtils.directoryType(OPTION_SETS);
    public static final String CONTENT_TYPE_ITEM = DbUtils.itemType(OPTION_SETS);

    @NonNull
    public static Uri optionSets() {
        return Uri.withAppendedPath(DbContract.AUTHORITY_URI, OPTION_SETS);
    }

    @NonNull
    public static Uri optionSets(long id) {
        return ContentUris.withAppendedId(optionSets(), id);
    }

    public interface Columns extends BaseIdentifiableObjectContract.Columns {
        String VERSION = "version";
        String VALUE_TYPE = "valueType";
    }
}
