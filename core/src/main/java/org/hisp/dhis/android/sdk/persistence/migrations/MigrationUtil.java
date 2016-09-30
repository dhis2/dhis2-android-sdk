package org.hisp.dhis.android.sdk.persistence.migrations;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment$Table;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.Event$Table;

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
     * This is run only once
     */
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

    /*
     * Backend requires completed dates for completed events.
     * This is run only once
     */
    public static void fixInvalidCompletedDates() {
        Log.d("DB migration", "Setting completedDate on completed Events");
        List<Event> completedEvents = new Select().from(Event.class)
                .where(Condition.column(Event$Table.STATUS).eq(Event.STATUS_COMPLETED))
                .and(Condition.column(Event$Table.COMPLETEDDATE).isNull()).queryList();
        for (Event completedEvent : completedEvents) {
            completedEvent.setCompletedDate(completedEvent.getLastUpdated());
            completedEvent.save();
            Log.d("DB migration", "Event " + completedEvent.getUid() + ": Completed date set to " + completedEvent.getLastUpdated());
        }
        Log.d("DB migration", "Migration done");
    }

    public static void migrateExistingData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("migrationFlags", context.MODE_PRIVATE);

        if (preferences.getBoolean("incidentDatesAreInvalid", true)) {
            fixInvalidIncidentDates();
            preferences.edit().putBoolean("incidentDatesAreInvalid", false).apply();
        }

        if (preferences.getBoolean("completedDatesAreInvalid", true)) {
            fixInvalidCompletedDates();
            preferences.edit().putBoolean("completedDatesAreInvalid", false).apply();
        }
    }
}
