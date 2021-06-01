/*
 *  Copyright (c) 2004-2021, University of Oslo
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
import java.util.ArrayList
import java.util.HashSet
import java.util.regex.Pattern
import javax.inject.Inject
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary
import org.hisp.dhis.android.core.imports.internal.ImportConflict

@Reusable
internal class DataValueImportHandler @Inject constructor(
    private val dataValueStore: DataValueStore
) {

    fun handleImportSummary(
        dataValueSet: DataValueSet?,
        dataValueImportSummary: DataValueImportSummary?
    ) {
        if (dataValueSet == null || dataValueImportSummary == null) {
            return
        }

        val state = when (dataValueImportSummary.importStatus()) {
            ImportStatus.ERROR -> State.ERROR
            ImportStatus.WARNING -> State.WARNING
            else -> State.SYNCED
        }

        if (state == State.WARNING) {
            handleDataValueWarnings(dataValueSet, dataValueImportSummary)
        } else {
            setStateToDataValues(state, dataValueSet.dataValues)
        }
    }

    private fun handleDataValueWarnings(
        dataValueSet: DataValueSet,
        dataValueImportSummary: DataValueImportSummary
    ) {
        getValuesWithConflicts(dataValueSet, dataValueImportSummary)?.let { dataValueConflicts ->
            setDataValueStates(dataValueSet, dataValueConflicts)
        } ?: setStateToDataValues(State.WARNING, dataValueSet.dataValues)
    }

    private fun getValuesWithConflicts(
        dataValueSet: DataValueSet,
        dataValueImportSummary: DataValueImportSummary
    ): Set<DataValue>? {
        val dataValueConflicts: MutableSet<DataValue> = HashSet()
        dataValueImportSummary.importConflicts()?.forEach { importConflict ->
            getDataValues(importConflict, dataValueSet.dataValues).let { dataValues ->
                if (dataValues.isEmpty()) {
                    return null
                }
                dataValueConflicts.addAll(dataValues)
            }
        }
        return dataValueConflicts
    }

    private fun setDataValueStates(
        dataValueSet: DataValueSet,
        dataValueConflicts: Set<DataValue>
    ) {
        val syncedValues = dataValueSet.dataValues.filter { dataValue ->
            !dataValueConflicts.contains(dataValue)
        }
        setStateToDataValues(State.WARNING, dataValueConflicts)
        setStateToDataValues(State.SYNCED, syncedValues)
    }

    private fun getDataValues(
        importConflict: ImportConflict,
        dataValues: Collection<DataValue>
    ): List<DataValue> {
        val patternStr = "(?<=:\\s)[a-zA-Z0-9]{11}"
        val pattern = Pattern.compile(patternStr)
        val matcher = pattern.matcher(importConflict.value())

        val foundDataValues: MutableList<DataValue> = ArrayList()

        if (matcher.find()) {
            val value = importConflict.`object`()
            val dataElementUid = matcher.group(0)
            for (dataValue in dataValues) {
                if (dataValue.value() == value && dataValue.dataElement() == dataElementUid) {
                    foundDataValues.add(dataValue)
                }
            }
        }
        return foundDataValues
    }

    private fun setStateToDataValues(state: State, dataValues: Collection<DataValue>) {
        for (dataValue in dataValues) {
            if (dataValueStore.isDataValueBeingUpload(dataValue)) {
                if (state == State.SYNCED && dataValueStore.isDeleted(dataValue)) {
                    dataValueStore.deleteWhere(dataValue)
                } else {
                    dataValueStore.setState(dataValue, state)
                }
            }
        }
    }
}
