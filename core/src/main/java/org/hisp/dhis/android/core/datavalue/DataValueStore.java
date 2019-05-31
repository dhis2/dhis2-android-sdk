/*
 * Copyright (c) 2004-2019, University of Oslo
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

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilder;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Collection;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@SuppressWarnings("PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal")
public class DataValueStore extends ObjectWithoutUidStoreImpl<DataValue> {

    private static final StatementBinder<DataValue> BINDER = (dataValue, sqLiteStatement) -> {
        sqLiteBind(sqLiteStatement, 1, dataValue.dataElement());
        sqLiteBind(sqLiteStatement, 2, dataValue.period());
        sqLiteBind(sqLiteStatement, 3, dataValue.organisationUnit());
        sqLiteBind(sqLiteStatement, 4, dataValue.categoryOptionCombo());
        sqLiteBind(sqLiteStatement, 5, dataValue.attributeOptionCombo());
        sqLiteBind(sqLiteStatement, 6, dataValue.value());
        sqLiteBind(sqLiteStatement, 7, dataValue.storedBy());
        sqLiteBind(sqLiteStatement, 8, dataValue.created());
        sqLiteBind(sqLiteStatement, 9, dataValue.lastUpdated());
        sqLiteBind(sqLiteStatement, 10, dataValue.comment());
        sqLiteBind(sqLiteStatement, 11, dataValue.followUp());
        sqLiteBind(sqLiteStatement, 12, dataValue.state());
    };

    private static final WhereStatementBinder<DataValue> WHERE_UPDATE_BINDER
            = (dataValue, sqLiteStatement) -> {
        sqLiteBind(sqLiteStatement, 13, dataValue.dataElement());
        sqLiteBind(sqLiteStatement, 14, dataValue.period());
        sqLiteBind(sqLiteStatement, 15, dataValue.organisationUnit());
        sqLiteBind(sqLiteStatement, 16, dataValue.categoryOptionCombo());
        sqLiteBind(sqLiteStatement, 17, dataValue.attributeOptionCombo());
    };

    private static final WhereStatementBinder<DataValue> WHERE_DELETE_BINDER
            = (dataValue, sqLiteStatement) -> {
        sqLiteBind(sqLiteStatement, 1, dataValue.dataElement());
        sqLiteBind(sqLiteStatement, 2, dataValue.period());
        sqLiteBind(sqLiteStatement, 3, dataValue.organisationUnit());
        sqLiteBind(sqLiteStatement, 4, dataValue.categoryOptionCombo());
        sqLiteBind(sqLiteStatement, 5, dataValue.attributeOptionCombo());
    };

    private DataValueStore(DatabaseAdapter databaseAdapter, SQLStatementBuilder builder) {
        super(databaseAdapter, builder, BINDER, WHERE_UPDATE_BINDER, WHERE_DELETE_BINDER, DataValue::create);
    }

    public static DataValueStore create(DatabaseAdapter databaseAdapter) {

        SQLStatementBuilder sqlStatementBuilder =
                new SQLStatementBuilder(DataValueTableInfo.TABLE_INFO.name(),
                        DataValueTableInfo.TABLE_INFO.columns());

        return new DataValueStore(databaseAdapter, sqlStatementBuilder);
    }

    Collection<DataValue> getDataValuesWithState(State state) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(DataValue.Columns.STATE, state.name()).build();
        return selectWhere(whereClause);
    }

    /**
     * @param dataValue DataValue element you want to update
     * @param newState The new state to be set for the DataValue
     */
    public void setState(DataValue dataValue, State newState) {

        DataValue updatedDataValue = dataValue.toBuilder().state(newState).build();

        updateWhere(updatedDataValue);
    }

    public boolean exists(DataValue dataValue) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(DataValueFields.DATA_ELEMENT, dataValue.dataElement())
                .appendKeyStringValue(DataValueFields.PERIOD, dataValue.period())
                .appendKeyStringValue(DataValueTableInfo.ORGANISATION_UNIT, dataValue.organisationUnit())
                .appendKeyStringValue(DataValueFields.CATEGORY_OPTION_COMBO, dataValue.categoryOptionCombo())
                .appendKeyStringValue(DataValueFields.ATTRIBUTE_OPTION_COMBO, dataValue.attributeOptionCombo())
                .build();
        return selectWhere(whereClause).size() > 0;
    }
}