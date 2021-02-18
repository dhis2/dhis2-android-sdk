package org.hisp.dhis.android.core.settings;

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.CoreColumns;
import org.hisp.dhis.android.core.common.IdentifiableColumns;

public class CompletionSpinnerTableInfo {

    private CompletionSpinnerTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {
        @Override
        public String name() {
            return "CompletionSpinner";
        }

        @Override
        public CoreColumns columns() {
            return new Columns();
        }
    };

    public static class Columns extends CoreColumns {
        public static final String UID = IdentifiableColumns.UID;
        public static final String VISIBLE = "visible";

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    UID,
                    VISIBLE
            );
        }
    }
}
