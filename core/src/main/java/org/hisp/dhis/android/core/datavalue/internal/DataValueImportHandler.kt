/*
 *  Copyright (c) 2004-2022, University of Oslo
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

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.State.ERROR
import org.hisp.dhis.android.core.common.State.SYNCED
import org.hisp.dhis.android.core.common.State.WARNING
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.DataValueConflict
import org.hisp.dhis.android.core.datavalue.DataValueConflictTableInfo
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary
import org.hisp.dhis.android.core.imports.internal.ImportConflict

@Reusable
internal class DataValueImportHandler @Inject constructor(
    private val dataValueStore: DataValueStore,
    private val dataValueConflictParser: DataValueConflictParser,
    private val dataValueConflictStore: ObjectStore<DataValueConflict>
) {

    fun handleImportSummary(
        dataValueSet: DataValueSet?,
        dataValueImportSummary: DataValueImportSummary?
    ) {
        if (dataValueSet == null || dataValueImportSummary == null) {
            return
        }

        val state = when (dataValueImportSummary.importStatus()) {
            ImportStatus.ERROR -> ERROR
            ImportStatus.WARNING -> WARNING
            else -> SYNCED
        }

        deleteDataValueConflicts(dataValueSet.dataValues)

        if (state == WARNING) {
            handleDataValueWarnings(dataValueSet.dataValues, dataValueImportSummary)
        } else {
            setStateToDataValues(state, dataValueSet.dataValues)
        }
    }

    private fun deleteDataValueConflicts(dataValues: List<DataValue>) {
        dataValues.forEach { dataValue ->
            val whereClause = WhereClauseBuilder()
                .appendKeyStringValue(
                    DataValueConflictTableInfo.Columns.ATTRIBUTE_OPTION_COMBO,
                    dataValue.attributeOptionCombo()
                )
                .appendKeyStringValue(
                    DataValueConflictTableInfo.Columns.CATEGORY_OPTION_COMBO,
                    dataValue.categoryOptionCombo()
                )
                .appendKeyStringValue(
                    DataValueConflictTableInfo.Columns.DATA_ELEMENT,
                    dataValue.dataElement()
                )
                .appendKeyStringValue(
                    DataValueConflictTableInfo.Columns.PERIOD,
                    dataValue.period()
                )
                .appendKeyStringValue(
                    DataValueConflictTableInfo.Columns.ORG_UNIT,
                    dataValue.organisationUnit()
                ).build()
            dataValueConflictStore.deleteWhereIfExists(whereClause)
        }
    }

    private fun handleDataValueWarnings(
        dataValues: List<DataValue>,
        dataValueImportSummary: DataValueImportSummary
    ) {
        getValuesWithConflicts(
            dataValues,
            dataValueImportSummary.importConflicts()
        )?.let { dataValueConflicts ->
            setDataValueStates(dataValues, dataValueConflicts)
        } ?: setStateToDataValues(WARNING, dataValues)
    }

    private fun getValuesWithConflicts(
        dataValues: List<DataValue>,
        importConflicts: List<ImportConflict>?
    ): Set<DataValue>? {
        val dataValueImportConflicts: MutableList<DataValueConflict> = mutableListOf()
        importConflicts?.forEach { importConflict ->

            val valuesPerConflict = dataValueConflictParser
                .getDataValueConflicts(importConflict, dataValues)

            if (valuesPerConflict.isEmpty()) {
                return null
            }
            dataValueImportConflicts.addAll(valuesPerConflict)
        }

        dataValueConflictStore.insert(dataValueImportConflicts)

        return dataValueImportConflicts.mapNotNull { dataValueConflict ->
            dataValues.find { dataValue ->
                dataValue.dataElement().equals(dataValueConflict.dataElement()) &&
                    dataValue.period().equals(dataValueConflict.period()) &&
                    dataValue.organisationUnit().equals(dataValueConflict.orgUnit()) &&
                    dataValue.attributeOptionCombo().equals(dataValueConflict.attributeOptionCombo()) &&
                    dataValue.categoryOptionCombo().equals(dataValueConflict.categoryOptionCombo())
            }
        }.toSet()
    }

    private fun setDataValueStates(
        dataValues: List<DataValue>,
        dataValueConflicts: Set<DataValue>
    ) {
        val syncedValues = dataValues.filter { dataValue ->
            !dataValueConflicts.contains(dataValue)
        }
        setStateToDataValues(WARNING, dataValueConflicts)
        setStateToDataValues(SYNCED, syncedValues)
    }

    private fun setStateToDataValues(state: State, dataValues: Collection<DataValue>) {
        for (dataValue in dataValues) {
            if (dataValueStore.isDataValueBeingUpload(dataValue)) {
                if (state == SYNCED && dataValueStore.isDeleted(dataValue)) {
                    dataValueStore.deleteWhere(dataValue)
                } else {
                    dataValueStore.setState(dataValue, state)
                }
            }
        }
    }
}
