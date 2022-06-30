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
package org.hisp.dhis.android.core.fileresource.internal

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.DataElementTableInfo
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.DataValueTableInfo
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore
import org.hisp.dhis.android.core.fileresource.FileResourceValueType
import org.hisp.dhis.android.core.trackedentity.*
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore

@Reusable
internal class FileResourceDownloadCallHelper @Inject constructor(
    private val dataElementStore: IdentifiableObjectStore<DataElement>,
    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore,
    private val trackedEntityAttributeStore: IdentifiableObjectStore<TrackedEntityAttribute>,
    private val trackedEntityDataValueStore: TrackedEntityDataValueStore,
    private val dataValueStore: DataValueStore
) {

    fun getMissingTrackerAttributeValues(
        params: FileResourceDownloadParams,
        existingFileResources: List<String>
    ): List<TrackedEntityAttributeValue> {
        // TODO Download files for TrackedEntityAttributes
        val valueTypes = params.valueTypes.filter { it == FileResourceValueType.IMAGE }

        val attributeUidsWhereClause = WhereClauseBuilder()
            .appendInKeyEnumValues(TrackedEntityAttributeTableInfo.Columns.VALUE_TYPE, valueTypes)
            .build()
        val trackedEntityAttributeUids = trackedEntityAttributeStore.selectUidsWhere(attributeUidsWhereClause)
        val attributeValuesWhereClause = WhereClauseBuilder()
            .appendInKeyStringValues(
                TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE,
                trackedEntityAttributeUids
            )
            .appendNotInKeyStringValues(
                TrackedEntityAttributeValueTableInfo.Columns.VALUE,
                existingFileResources
            )
            .build()
        return trackedEntityAttributeValueStore.selectWhere(attributeValuesWhereClause)
    }

    fun getMissingTrackerDataValues(
        params: FileResourceDownloadParams,
        existingFileResources: List<String>
    ): List<TrackedEntityDataValue> {
        val dataElementUidsWhereClause = WhereClauseBuilder()
            .appendInKeyEnumValues(DataElementTableInfo.Columns.VALUE_TYPE, params.valueTypes)
            .appendKeyStringValue(DataElementTableInfo.Columns.DOMAIN_TYPE, "TRACKER")
            .build()
        val dataElementUids = dataElementStore.selectUidsWhere(dataElementUidsWhereClause)
        val dataValuesWhereClause = WhereClauseBuilder()
            .appendInKeyStringValues(TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT, dataElementUids)
            .appendNotInKeyStringValues(TrackedEntityDataValueTableInfo.Columns.VALUE, existingFileResources)
            .build()
        return trackedEntityDataValueStore.selectWhere(dataValuesWhereClause)
    }

    fun getMissingAggregatedDataValues(
        params: FileResourceDownloadParams,
        existingFileResources: List<String>
    ): List<DataValue> {
        val dataElementUidsWhereClause = WhereClauseBuilder()
            .appendInKeyEnumValues(DataElementTableInfo.Columns.VALUE_TYPE, params.valueTypes)
            .appendKeyStringValue(DataElementTableInfo.Columns.DOMAIN_TYPE, "AGGREGATE")
            .build()
        val dataElementUids = dataElementStore.selectUidsWhere(dataElementUidsWhereClause)
        val dataValuesWhereClause = WhereClauseBuilder()
            .appendInKeyStringValues(DataValueTableInfo.Columns.DATA_ELEMENT, dataElementUids)
            .appendNotInKeyStringValues(DataValueTableInfo.Columns.VALUE, existingFileResources)
            .build()
        return dataValueStore.selectWhere(dataValuesWhereClause)
    }
}
