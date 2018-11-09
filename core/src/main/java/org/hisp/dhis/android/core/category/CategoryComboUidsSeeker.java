package org.hisp.dhis.android.core.category;

import android.database.Cursor;

import org.hisp.dhis.android.core.arch.db.MultipleTableQueryBuilder;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataelement.DataElementTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetElementLinkTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetFields;
import org.hisp.dhis.android.core.dataset.DataSetTableInfo;
import org.hisp.dhis.android.core.program.ProgramTableInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryComboUidsSeeker {

    private final DatabaseAdapter databaseAdapter;

    public CategoryComboUidsSeeker(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
    }

    public Set<String> seekUids() {
        List<String> tableNames = Arrays.asList(
                ProgramTableInfo.TABLE_INFO.name(),
                DataSetTableInfo.TABLE_INFO.name(),
                DataElementTableInfo.TABLE_INFO.name(),
                DataSetElementLinkTableInfo.TABLE_INFO.name());

        String query = new MultipleTableQueryBuilder().generateQuery(DataSetFields.CATEGORY_COMBO, tableNames).build();

        Cursor cursor = databaseAdapter.query(query);
        Set<String> categoryCombos = new HashSet<>();
        try {
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    categoryCombos.add(cursor.getString(0));
                }
                while(cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return categoryCombos;
    }
}