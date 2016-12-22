package org.hisp.dhis.android.core.option;

import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectContract;
import org.hisp.dhis.android.core.data.database.DbContract;
import org.hisp.dhis.android.core.data.database.DbUtils;

public class OptionContract {
    public static final String OPTIONS = "options";
    public static final String OPTIONS_ID = OPTIONS + "/#";

    public static final String CONTENT_TYPE_DIR = DbUtils.directoryType(OPTIONS);
    public static final String CONTENT_TYPE_ITEM = DbUtils.itemType(OPTIONS);

    @NonNull
    public static Uri options() {
        return Uri.withAppendedPath(DbContract.AUTHORITY_URI, OPTIONS);
    }

    @NonNull
    public static Uri options(long id) {
        return ContentUris.withAppendedId(options(), id);
    }

    public interface Columns extends BaseIdentifiableObjectContract.Columns {

    }

}
