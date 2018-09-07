/*
 * Copyright (c) 2004-2018, University of Oslo
 * All rights reserved.
 *
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
import net.sqlcipher.database.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.WhereStatementBinder;
import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStoreImpl;
import org.hisp.dhis.android.core.common.SQLStatementBuilder;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.utils.StoreUtils;

import java.util.ArrayList;
import java.util.Collection;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@SuppressWarnings("PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal")
public class DataValueStore extends ObjectWithoutUidStoreImpl<DataValueModel> {

    private static final String QUERY_WITH_STATE = "SELECT " +
            DataValueModel.Columns.DATA_ELEMENT + "," +
            DataValueModel.Columns.PERIOD + "," +
            DataValueModel.Columns.ORGANISATION_UNIT + "," +
            DataValueModel.Columns.CATEGORY_OPTION_COMBO + "," +
            DataValueModel.Columns.ATTRIBUTE_OPTION_COMBO + "," +
            DataValueModel.Columns.VALUE + "," +
            DataValueModel.Columns.STORED_BY + "," +
            DataValueModel.Columns.CREATED + "," +
            DataValueModel.Columns.LAST_UPDATED + "," +
            DataValueModel.Columns.COMMENT + "," +
            DataValueModel.Columns.FOLLOW_UP +
            " FROM " +
            DataValueModel.TABLE +
            " WHERE " +
            DataValueModel.Columns.STATE +
            " = ':state'";

    private DataValueStore(DatabaseAdapter databaseAdapter, SQLiteStatement insertStatement,
                           SQLiteStatement updateWhereStatement, SQLStatementBuilder builder) {

        super(databaseAdapter, insertStatement, updateWhereStatement,
                builder, BINDER, WHERE_UPDATE_BINDER);
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
        public void bindToStatement(@NonNull DataValueModel dataValueModel,
                                    @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 1, dataValueModel.dataElement());
            sqLiteBind(sqLiteStatement, 2, dataValueModel.period());
            sqLiteBind(sqLiteStatement, 3, dataValueModel.organisationUnit());
            sqLiteBind(sqLiteStatement, 4, dataValueModel.categoryOptionCombo());
            sqLiteBind(sqLiteStatement, 5, dataValueModel.attributeOptionCombo());
            sqLiteBind(sqLiteStatement, 6, dataValueModel.value());
            sqLiteBind(sqLiteStatement, 7, dataValueModel.storedBy());
            sqLiteBind(sqLiteStatement, 8, dataValueModel.created());
            sqLiteBind(sqLiteStatement, 9, dataValueModel.lastUpdated());
            sqLiteBind(sqLiteStatement, 10, dataValueModel.comment());
            sqLiteBind(sqLiteStatement, 11, dataValueModel.followUp());
            sqLiteBind(sqLiteStatement, 12, dataValueModel.state());
        }
    };

    private static final WhereStatementBinder<DataValueModel> WHERE_UPDATE_BINDER
            = new WhereStatementBinder<DataValueModel>() {
        @Override
        public void bindToUpdateWhereStatement(@NonNull DataValueModel dataValueModel,
                                               @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 13, dataValueModel.dataElement());
            sqLiteBind(sqLiteStatement, 14, dataValueModel.period());
            sqLiteBind(sqLiteStatement, 15, dataValueModel.organisationUnit());
            sqLiteBind(sqLiteStatement, 16, dataValueModel.categoryOptionCombo());
            sqLiteBind(sqLiteStatement, 17, dataValueModel.attributeOptionCombo());
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
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        StoreUtils.parse(cursor.getString(7)),
                        StoreUtils.parse(cursor.getString(8)),
                        cursor.getString(9),
                        cursor.getInt(10) == 1,
                        null
                );

                dataValues.add(dataValue);

            } while(cursor.moveToNext());
        }

        cursor.close();

        return dataValues;
    }

    /**
     * @param dataValue DataValue element you want to update
     * @param newState The new state to be set for the DataValue
     */
    public void setState(DataValue dataValue, State newState) {

        DataValueModel dataValueModel = new DataValueModelBuilder(newState).buildModel(dataValue);

        updateWhere(dataValueModel);
    }

}