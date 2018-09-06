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

package org.hisp.dhis.android.core.dataset;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.WhereStatementBinder;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStoreImpl;
import org.hisp.dhis.android.core.common.SQLStatementBuilder;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Collection;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class DataSetCompleteRegistrationStore extends
        ObjectWithoutUidStoreImpl<DataSetCompleteRegistration> {

    private DataSetCompleteRegistrationStore(DatabaseAdapter databaseAdapter, SQLiteStatement insertStatement,
                                             SQLiteStatement updateWhereStatement, SQLStatementBuilder builder) {

        super(databaseAdapter, insertStatement, updateWhereStatement,
                builder, BINDER, WHERE_UPDATE_BINDER);
    }

    public static DataSetCompleteRegistrationStore create(DatabaseAdapter databaseAdapter) {

        SQLStatementBuilder sqlStatementBuilder =
                new SQLStatementBuilder(DataSetCompleteRegistrationTableInfo.TABLE_INFO.name(),
                        DataSetCompleteRegistrationTableInfo.TABLE_INFO.columns());

        return new DataSetCompleteRegistrationStore(databaseAdapter, databaseAdapter.compileStatement(
                sqlStatementBuilder.insert()),
                databaseAdapter.compileStatement(sqlStatementBuilder.updateWhere()),
                sqlStatementBuilder);
    }

    private static final StatementBinder<DataSetCompleteRegistration> BINDER =
            new StatementBinder<DataSetCompleteRegistration>() {
        @Override
        public void bindToStatement(@NonNull DataSetCompleteRegistration dataSetCompleteRegistration,
                                    @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 1, dataSetCompleteRegistration.period());
            sqLiteBind(sqLiteStatement, 2, dataSetCompleteRegistration.dataSet());
            sqLiteBind(sqLiteStatement, 3, dataSetCompleteRegistration.organisationUnit());
            sqLiteBind(sqLiteStatement, 4, dataSetCompleteRegistration.attributeOptionCombo());
            sqLiteBind(sqLiteStatement, 5, dataSetCompleteRegistration.date());
            sqLiteBind(sqLiteStatement, 6, dataSetCompleteRegistration.storedBy());
            sqLiteBind(sqLiteStatement, 7, dataSetCompleteRegistration.state());
        }
    };

    private static final WhereStatementBinder<DataSetCompleteRegistration> WHERE_UPDATE_BINDER =
            new WhereStatementBinder<DataSetCompleteRegistration>() {
        @Override
        public void bindToUpdateWhereStatement(@NonNull DataSetCompleteRegistration dataSetCompleteRegistration,
                                               @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 8, dataSetCompleteRegistration.period());
            sqLiteBind(sqLiteStatement, 9, dataSetCompleteRegistration.dataSet());
            sqLiteBind(sqLiteStatement, 10, dataSetCompleteRegistration.organisationUnit());
            sqLiteBind(sqLiteStatement, 11, dataSetCompleteRegistration.attributeOptionCombo());
        }
    };

    Collection<DataSetCompleteRegistration> getDataSetCompleteRegistrationsWithState(State state) {
        String whereClause = DataSetCompleteRegistration.Columns.STATE + "='" + state.name() + "'";
        return selectWhereClause(DataSetCompleteRegistration.factory, whereClause);
    }

    /**
     * @param dataSetCompleteRegistration DataSetCompleteRegistration element you want to update
     * @param newState The new state to be set for the DataValue
     */
    public void setState(DataSetCompleteRegistration dataSetCompleteRegistration, State newState) {

        DataSetCompleteRegistration updatedDataSetCompleteRegistration
                = dataSetCompleteRegistration.toBuilder().state(newState).build();

        updateWhere(updatedDataSetCompleteRegistration);
    }

}
