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

package org.hisp.dhis.android.core.datavalue.internal.conflicts

import java.util.ArrayList
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.DataValueConflict
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore
import org.hisp.dhis.android.core.imports.internal.ImportConflict

internal class PastExpiryDateConflict(
    private val dataValueStore: DataValueStore,
    private val dataSetStore: IdentifiableObjectStore<DataSet>
) : LegacyDataValueImportConflictItem {

    override val regex: Regex
        get() = Regex("Current date is past expiry days for period (\\d+) and data set: (\\w{11})")

    override fun getDataValues(conflict: ImportConflict, dataValues: List<DataValue>): List<DataValueConflict> {
        val foundDataValuesConflicts: MutableList<DataValueConflict> = ArrayList()
        val period = conflict.`object`()
        val dataSetUid = regex.find(conflict.value())?.groupValues?.get(2)
        dataValues.forEach { dataValue ->
            if (dataValue.period() == period && dataValueStore.existsInDataSet(dataValue, dataSetUid)) {
                foundDataValuesConflicts.add(
                    getConflictBuilder(
                        dataValue = dataValue,
                        conflict = conflict,
                        displayDescription = getDisplayDescription(conflict, dataValue, dataSetUid!!)
                    ).build()
                )
            }
        }

        return foundDataValuesConflicts
    }

    private fun getDisplayDescription(conflict: ImportConflict, dataValue: DataValue, dataSetUid: String) =
        dataSetStore.selectByUid(dataSetUid)?.let { dataSet ->
            "Current date is past expiry days for period ${dataValue.period()} and data set: ${dataSet.displayName()}"
        } ?: conflict.value()
}
