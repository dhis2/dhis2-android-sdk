package org.hisp.dhis.android.sdk.persistence.migrations;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment$Table;

import java.util.List;

/**
 * Created by thomaslindsjorn on 28/07/16.
 */
public class MigrationUtil {

    private static SQLiteDatabase database;

    public static boolean columnExists(@NotNull Class tableClass, @NotNull String columnName) {
        Cursor dbCursor = database.query( // empty query just to get the column names for the table
                FlowManager.getTableName(tableClass), null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equals(columnName)) {
                return true;
            }
        }
        return false;
    }

    public static void setDatabase(SQLiteDatabase database) {
        MigrationUtil.database = database;
    }

    /*
    * Backend does not accept empty strings in incident dates. Set the values to null
    * */
    public static void fixInvalidIncidentDates() {

        List<Enrollment> enrollmentsWithEmptyIncidentDates =
                new Select().from(Enrollment.class).where(Condition.column(Enrollment$Table.INCIDENTDATE).eq("")).queryList();

        for (Enrollment enrollment : enrollmentsWithEmptyIncidentDates) {
            enrollment.setIncidentDate(null);
            enrollment.update();
            Log.d("DB migration", "Enrollment " + enrollment.getUid() + ": Incident date set to null");
        }

        Log.d("DB migration", "Migration done");
    }
}
