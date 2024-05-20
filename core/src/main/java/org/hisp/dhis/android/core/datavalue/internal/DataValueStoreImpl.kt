/*
 *  Copyright (c) 2004-2023, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.datavalue.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.DataValueByDataSetQueryHelper.dataValueKey
import org.hisp.dhis.android.core.datavalue.DataValueByDataSetQueryHelper.whereClause
import org.hisp.dhis.android.core.datavalue.DataValueTableInfo
import org.koin.core.annotation.Singleton

@Singleton
internal class DataValueStoreImpl(
    databaseAdapter: DatabaseAdapter,
) : DataValueStore,
    ObjectWithoutUidStoreImpl<DataValue>(
        databaseAdapter,
        DataValueTableInfo.TABLE_INFO,
        BINDER,
        WHERE_UPDATE_BINDER,
        WHERE_DELETE_BINDER,
        { cursor: Cursor? -> DataValue.create(cursor) },
    ) {

    override fun getDataValuesWithState(state: State): Collection<DataValue> {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(DataValueTableInfo.Columns.SYNC_STATE, state.name).build()
        return selectWhere(whereClause)
    }

    /**
     * @param dataValue DataValue element you want to update
     * @param newState  The new state to be set for the DataValue
     */
    override fun setState(dataValue: DataValue, newState: State) {
        val updatedDataValue = dataValue.toBuilder().syncState(newState).build()
        updateWhere(updatedDataValue)
    }

    override fun exists(dataValue: DataValue): Boolean {
        return selectWhere(uniqueWhereClauseBuilder(dataValue).build()).isNotEmpty()
    }

    override fun isDataValueBeingUpload(dataValue: DataValue): Boolean {
        val whereClause = uniqueWhereClauseBuilder(dataValue)
            .appendKeyStringValue(DataValueTableInfo.Columns.SYNC_STATE, State.UPLOADING)
            .build()
        return selectWhere(whereClause).isNotEmpty()
    }

    override fun isDeleted(dataValue: DataValue): Boolean {
        val whereClause = uniqueWhereClauseBuilder(dataValue)
            .appendKeyNumberValue(DataValueTableInfo.Columns.DELETED, 1)
            .build()
        return selectWhere(whereClause).isNotEmpty()
    }

    private fun uniqueWhereClauseBuilder(dataValue: DataValue): WhereClauseBuilder {
        return WhereClauseBuilder()
            .appendKeyStringValue(DataValueTableInfo.Columns.DATA_ELEMENT, dataValue.dataElement()!!)
            .appendKeyStringValue(DataValueTableInfo.Columns.PERIOD, dataValue.period()!!)
            .appendKeyStringValue(DataValueTableInfo.Columns.ORGANISATION_UNIT, dataValue.organisationUnit()!!)
            .appendKeyStringValue(DataValueTableInfo.Columns.CATEGORY_OPTION_COMBO, dataValue.categoryOptionCombo()!!)
            .appendKeyStringValue(DataValueTableInfo.Columns.ATTRIBUTE_OPTION_COMBO, dataValue.attributeOptionCombo()!!)
    }

    override fun existsInDataSet(dataValue: DataValue, dataSetUid: String): Boolean {
        val whereClauseBuilder = uniqueWhereClauseBuilder(dataValue)
            .appendInSubQuery(
                dataValueKey,
                whereClause(dataSetUid),
            )
        return selectWhere(whereClauseBuilder.build()).isNotEmpty()
    }

    companion object {
        private val BINDER = StatementBinder { dataValue: DataValue, w: StatementWrapper ->
            w.bind(1, dataValue.dataElement())
            w.bind(2, dataValue.period())
            w.bind(3, dataValue.organisationUnit())
            w.bind(4, dataValue.categoryOptionCombo())
            w.bind(5, dataValue.attributeOptionCombo())
            w.bind(6, dataValue.value())
            w.bind(7, dataValue.storedBy())
            w.bind(8, dataValue.created())
            w.bind(9, dataValue.lastUpdated())
            w.bind(10, dataValue.comment())
            w.bind(11, dataValue.followUp())
            w.bind(12, dataValue.syncState())
            w.bind(13, dataValue.deleted())
        }
        private val WHERE_UPDATE_BINDER = WhereStatementBinder { dataValue: DataValue, w: StatementWrapper ->
            w.bind(14, dataValue.dataElement())
            w.bind(15, dataValue.period())
            w.bind(16, dataValue.organisationUnit())
            w.bind(17, dataValue.categoryOptionCombo())
            w.bind(18, dataValue.attributeOptionCombo())
        }
        private val WHERE_DELETE_BINDER = WhereStatementBinder { dataValue: DataValue, w: StatementWrapper ->
            w.bind(1, dataValue.dataElement())
            w.bind(2, dataValue.period())
            w.bind(3, dataValue.organisationUnit())
            w.bind(4, dataValue.categoryOptionCombo())
            w.bind(5, dataValue.attributeOptionCombo())
        }
    }
}
