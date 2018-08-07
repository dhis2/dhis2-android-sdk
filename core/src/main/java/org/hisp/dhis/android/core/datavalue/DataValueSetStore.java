package org.hisp.dhis.android.core.datavalue;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.utils.StoreUtils;

import java.util.ArrayList;
import java.util.Collection;

public class DataValueSetStore {

    private static final String QUERY_WITH_STATE = "SELECT " +
            DataValueModel.Columns.DATA_ELEMENT + "," +
            DataValueModel.Columns.PERIOD + "," +
            DataValueModel.Columns.ORGANISATION_UNIT + "," +
            DataValueModel.Columns.VALUE +
            " FROM " +
            DataValueModel.TABLE +
            " WHERE " +
            DataValueModel.Columns.STATE +
            " = ':state'";

    private static final String UPDATE_STATE = "UPDATE "
            + DataValueModel.TABLE +
            " SET " +
            DataValueModel.Columns.STATE + " = ? " +
            " WHERE " +
            DataValueModel.Columns.DATA_ELEMENT + " = ?;";


    private final DatabaseAdapter databaseAdapter;
    private final SQLiteStatement updateStateStatement;


    public DataValueSetStore(DatabaseAdapter databaseAdapter) {

        this.databaseAdapter = databaseAdapter;
        this.updateStateStatement = databaseAdapter.compileStatement(UPDATE_STATE);
    }

    public Collection<DataValue> getDataValuesWithState(State state) {
        return queryDataValuesWithState(state);
    }

    private Collection<DataValue> queryDataValuesWithState(State state) {

        String query = QUERY_WITH_STATE.replace(":state", state.name());

        Cursor cursor = databaseAdapter.query(query);

        return map(cursor);
    }

    private Collection<DataValue> map(Cursor cursor) {

        Collection<DataValue> dataValues = new ArrayList<>();

        cursor.moveToFirst();

        if (cursor.getCount() > 0) {

            do {

                DataValue dataValue = DataValue.create(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        null,
                        null,
                        cursor.getString(3),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

                dataValues.add(dataValue);

            } while(cursor.moveToNext());
        }

        return dataValues;
    }

    /**
     * @param dataElementUid UID from the DataElement related to the DataValue you want to set
     * @param newState The new state to be set for the DataValue
     *
     * @return True if any DataValue has been updated
     */
    @SuppressWarnings("PMD.UselessParentheses")
    public boolean setState(String dataElementUid, State newState) {

        StoreUtils.sqLiteBind(updateStateStatement, 1, newState.name());
        StoreUtils.sqLiteBind(updateStateStatement, 2, dataElementUid);

        int updatedRows = databaseAdapter.executeUpdateDelete(DataValueModel.TABLE, updateStateStatement);
        updateStateStatement.clearBindings();

        return (updatedRows > 0);
    }
}
