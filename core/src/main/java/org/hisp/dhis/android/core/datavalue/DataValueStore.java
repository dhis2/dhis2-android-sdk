/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.datavalue;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.WhereStatementBinder;
import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStoreImpl;
import org.hisp.dhis.android.core.common.SQLStatementBuilder;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.Collection;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class DataValueStore extends ObjectWithoutUidStoreImpl<DataValueModel> {

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

    private final SQLiteStatement updateStateStatement;



    private DataValueStore(DatabaseAdapter databaseAdapter, SQLiteStatement insertStatement,
                           SQLiteStatement updateWhereStatement, SQLStatementBuilder builder) {

        super(databaseAdapter, insertStatement, updateWhereStatement,
                builder, BINDER, WHERE_UPDATE_BINDER);

        this.updateStateStatement = databaseAdapter.compileStatement(UPDATE_STATE);
    }

    public static DataValueStore create(DatabaseAdapter databaseAdapter) {

        BaseModel.Columns columns = new DataValueModel.Columns();

        SQLStatementBuilder sqlStatementBuilder =
                new SQLStatementBuilder(DataValueModel.TABLE, columns);

        return new DataValueStore(databaseAdapter, databaseAdapter.compileStatement(
                sqlStatementBuilder.insert()),
                databaseAdapter.compileStatement(sqlStatementBuilder.updateWhere()),
                sqlStatementBuilder);
    }

    private static final StatementBinder<DataValueModel> BINDER = new StatementBinder<DataValueModel>() {
        @Override
        public void bindToStatement(@NonNull DataValueModel o, @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 1, o.dataElement());
            sqLiteBind(sqLiteStatement, 2, o.period());
            sqLiteBind(sqLiteStatement, 3, o.organisationUnit());
            sqLiteBind(sqLiteStatement, 4, o.categoryOptionCombo());
            sqLiteBind(sqLiteStatement, 5, o.attributeOptionCombo());
            sqLiteBind(sqLiteStatement, 6, o.value());
            sqLiteBind(sqLiteStatement, 7, o.storedBy());
            sqLiteBind(sqLiteStatement, 8, o.created());
            sqLiteBind(sqLiteStatement, 9, o.lastUpdated());
            sqLiteBind(sqLiteStatement, 10, o.comment());
            sqLiteBind(sqLiteStatement, 11, o.followUp());
            sqLiteBind(sqLiteStatement, 12, o.state());
        }
    };

    private static final WhereStatementBinder<DataValueModel> WHERE_UPDATE_BINDER
            = new WhereStatementBinder<DataValueModel>() {
        @Override
        public void bindToUpdateWhereStatement(@NonNull DataValueModel o, @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 13, o.dataElement());
            sqLiteBind(sqLiteStatement, 14, o.period());
            sqLiteBind(sqLiteStatement, 15, o.organisationUnit());
            sqLiteBind(sqLiteStatement, 16, o.categoryOptionCombo());
            sqLiteBind(sqLiteStatement, 17, o.attributeOptionCombo());
        }
    };

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

        sqLiteBind(updateStateStatement, 1, newState.name());
        sqLiteBind(updateStateStatement, 2, dataElementUid);

        int updatedRows = databaseAdapter.executeUpdateDelete(DataValueModel.TABLE, updateStateStatement);
        updateStateStatement.clearBindings();

        return (updatedRows > 0);
    }

}