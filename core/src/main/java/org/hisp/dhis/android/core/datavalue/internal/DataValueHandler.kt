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

import java.util.ArrayList
import java.util.Arrays
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.ObjectWithoutUidHandlerImpl
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.DataValueTableInfo

internal class DataValueHandler(store: ObjectWithoutUidStore<DataValue>) : ObjectWithoutUidHandlerImpl<DataValue>(
    store
) {
    override fun deleteOrPersist(o: DataValue): HandleAction {
        return if (CollectionsHelper.isDeleted(o)) {
            store.deleteWhereIfExists(o)
            HandleAction.Delete
        } else {
            store.updateOrInsertWhere(o)
        }
    }

    override fun beforeCollectionHandled(oCollection: Collection<DataValue>): Collection<DataValue> {
        val dataValuesPendingToSync = dataValuesPendingToSync
        val dataValuesToUpdate: MutableList<DataValue> = ArrayList()
        for (dataValue in oCollection) {
            if (!containsDataValue(dataValuesPendingToSync, dataValue)) {
                dataValuesToUpdate.add(dataValue)
            }
        }
        return dataValuesToUpdate
    }

    @Suppress("TooGenericExceptionCaught")
    private fun containsDataValue(dataValues: Collection<DataValue>, target: DataValue): Boolean {
        return try {
            for (item in dataValues) {
                @Suppress("ComplexCondition")
                if (item.dataElement() == target.dataElement() &&
                    item.organisationUnit() == target.organisationUnit() &&
                    item.period() == target.period() &&
                    item.attributeOptionCombo() == target.attributeOptionCombo() &&
                    item.categoryOptionCombo() == target.categoryOptionCombo()
                ) {
                    return true
                }
            }
            false
        } catch (e: NullPointerException) {
            false
        }
    }

    private val dataValuesPendingToSync: List<DataValue>
        get() {
            val whereClause = WhereClauseBuilder()
                .appendNotInKeyStringValues(
                    DataValueTableInfo.Columns.SYNC_STATE,
                    Arrays.asList(State.SYNCED.name, State.SYNCED_VIA_SMS.name)
                )
                .build()
            return store.selectWhere(whereClause)
        }
}
