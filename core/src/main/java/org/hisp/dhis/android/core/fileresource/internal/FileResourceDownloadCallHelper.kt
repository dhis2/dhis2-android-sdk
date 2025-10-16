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
package org.hisp.dhis.android.core.fileresource.internal

import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.dataset.internal.DataSetElementStore
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.fileresource.FileResourceValueType
import org.hisp.dhis.android.core.icon.CustomIcon
import org.hisp.dhis.android.core.icon.internal.CustomIconStore
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.persistence.common.querybuilders.WhereClauseBuilder
import org.hisp.dhis.android.persistence.dataelement.DataElementTableInfo
import org.hisp.dhis.android.persistence.dataset.DataSetDataElementLinkTableInfo
import org.hisp.dhis.android.persistence.datavalue.DataValueTableInfo
import org.hisp.dhis.android.persistence.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.persistence.event.EventTableInfo
import org.hisp.dhis.android.persistence.icon.CustomIconTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeValueTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityDataValueTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceTableInfo
import org.koin.core.annotation.Singleton

@Suppress("LongParameterList")
@Singleton
internal class FileResourceDownloadCallHelper(
    private val dataElementStore: DataElementStore,
    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore,
    private val trackedEntityAttributeStore: TrackedEntityAttributeStore,
    private val trackedEntityDataValueStore: TrackedEntityDataValueStore,
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore,
    private val eventStore: EventStore,
    private val dataSetElementStore: DataSetElementStore,
    private val dataValueStore: DataValueStore,
    private val customIconStore: CustomIconStore,
    private val dhisVersionManager: DHISVersionManager,
) {

    suspend fun getMissingTrackerAttributeValues(
        params: FileResourceDownloadParams,
        existingFileResources: List<String>,
    ): List<MissingTrackerAttributeValue> {
        val fileTypes = params.valueTypes.filter { type ->
            dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_40) || type == FileResourceValueType.IMAGE
        }

        val attributesWhereClause = WhereClauseBuilder()
            .appendInKeyEnumValues(TrackedEntityAttributeTableInfo.Columns.VALUE_TYPE, fileTypes.map { it.valueType })
            .build()

        val trackedEntityAttributes = trackedEntityAttributeStore.selectWhere(attributesWhereClause)

        val attributeValuesWhereClauseBuilder = WhereClauseBuilder().apply {
            val attributes = trackedEntityAttributes.map { it.uid() }
            appendInKeyStringValues(TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE, attributes)
            appendNotInKeyStringValues(TrackedEntityAttributeValueTableInfo.Columns.VALUE, existingFileResources)

            val teiParamsClause = getTrackedEntityWhereClauseFromParams(params)

            if (teiParamsClause.isEmpty.not()) {
                val teis = trackedEntityInstanceStore.selectUidsWhere(teiParamsClause.build())
                appendInKeyStringValues(TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE, teis)
            }
        }

        return trackedEntityAttributeValueStore.selectWhere(attributeValuesWhereClauseBuilder.build())
            .map { av ->
                val type = trackedEntityAttributes.find { it.uid() == av.trackedEntityAttribute() }!!.valueType()!!
                MissingTrackerAttributeValue(av, type)
            }
    }

    suspend fun getMissingTrackerDataValues(
        params: FileResourceDownloadParams,
        existingFileResources: List<String>,
    ): List<TrackedEntityDataValue> {
        val dataElementUids = dataElementStore.selectUidsWhere(
            WhereClauseBuilder()
                .appendInKeyEnumValues(DataElementTableInfo.Columns.VALUE_TYPE, params.valueTypes.map { it.valueType })
                .appendKeyStringValue(DataElementTableInfo.Columns.DOMAIN_TYPE, "TRACKER")
                .build(),
        )

        val dataValuesWhereClause = WhereClauseBuilder().apply {
            appendInKeyStringValues(TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT, dataElementUids)
            appendNotInKeyStringValues(TrackedEntityDataValueTableInfo.Columns.VALUE, existingFileResources)

            val eventWhereClause = getEventWhereClauseFromParams(params)

            if (eventWhereClause.isEmpty.not()) {
                val eventUids = eventStore.selectUidsWhere(eventWhereClause.build())
                appendInKeyStringValues(TrackedEntityDataValueTableInfo.Columns.EVENT, eventUids)
            }
        }.build()

        return trackedEntityDataValueStore.selectWhere(dataValuesWhereClause)
    }

    suspend fun getMissingAggregatedDataValues(
        params: FileResourceDownloadParams,
        existingFileResources: List<String>,
    ): List<DataValue> {
        val dataElementUidsWhereClause = WhereClauseBuilder()
            .appendInKeyEnumValues(DataElementTableInfo.Columns.VALUE_TYPE, params.valueTypes.map { it.valueType })
            .appendKeyStringValue(DataElementTableInfo.Columns.DOMAIN_TYPE, "AGGREGATE")
            .build()

        val dataElementUids = dataElementStore.selectUidsWhere(dataElementUidsWhereClause).toMutableList()

        val filteredDataElementUids = params.dataSetUids.takeIf { it.isNotEmpty() }
            ?.let { dataSetUids ->
                val dataSetElementsWhereClause = WhereClauseBuilder()
                    .appendInKeyStringValues(DataSetDataElementLinkTableInfo.Columns.DATA_SET, dataSetUids)
                    .build()

                val dataElementUidsFromDataSet = dataSetElementStore.selectWhere(dataSetElementsWhereClause)
                    .map { it.dataElement().uid() }

                dataElementUids.intersect(dataElementUidsFromDataSet.toSet()).toList()
            } ?: dataElementUids

        val dataValuesWhereClause = WhereClauseBuilder()
            .appendInKeyStringValues(DataValueTableInfo.Columns.DATA_ELEMENT, filteredDataElementUids)
            .appendNotInKeyStringValues(DataValueTableInfo.Columns.VALUE, existingFileResources)
            .build()

        return dataValueStore.selectWhere(dataValuesWhereClause)
    }

    suspend fun getMissingCustomIcons(
        existingFileResources: List<String>,
    ): List<CustomIcon> {
        val customIconsWhereClause = WhereClauseBuilder()
            .appendNotInKeyStringValues(CustomIconTableInfo.Columns.FILE_RESOURCE, existingFileResources)
            .build()
        return customIconStore.selectWhere(customIconsWhereClause)
    }

    companion object {
        fun getTrackedEntityWhereClauseFromParams(params: FileResourceDownloadParams): WhereClauseBuilder {
            return WhereClauseBuilder().apply {
                if (params.trackedEntityUids.isNotEmpty()) {
                    appendInKeyStringValues(
                        TrackedEntityInstanceTableInfo.Columns.UID,
                        params.trackedEntityUids,
                    )
                }
                if (params.programUids.isNotEmpty()) {
                    val inProgramsWhere = WhereClauseBuilder()
                        .appendInKeyStringValues(
                            EnrollmentTableInfo.Columns.PROGRAM,
                            params.programUids,
                        ).build()

                    val programSubQuery = "SELECT ${EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE} " +
                        "FROM ${EnrollmentTableInfo.TABLE_INFO.name()} " +
                        "WHERE $inProgramsWhere"

                    appendInSubQuery(TrackedEntityInstanceTableInfo.Columns.UID, programSubQuery)
                }
            }
        }

        fun getEventWhereClauseFromParams(params: FileResourceDownloadParams): WhereClauseBuilder {
            return WhereClauseBuilder().apply {
                if (params.eventUids.isNotEmpty()) {
                    appendInKeyStringValues(EventTableInfo.Columns.UID, params.eventUids)
                }

                if (params.programUids.isNotEmpty()) {
                    appendInKeyStringValues(EventTableInfo.Columns.PROGRAM, params.programUids)
                }

                if (params.trackedEntityUids.isNotEmpty()) {
                    val inTrackedEntitiesWhere = WhereClauseBuilder()
                        .appendInKeyStringValues(
                            EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                            params.trackedEntityUids,
                        ).build()
                    val enrollmentSubQuery = "SELECT ${EnrollmentTableInfo.Columns.UID} " +
                        "FROM ${EnrollmentTableInfo.TABLE_INFO.name()} " +
                        "WHERE $inTrackedEntitiesWhere"

                    appendInSubQuery(EventTableInfo.Columns.ENROLLMENT, enrollmentSubQuery)
                }
            }
        }
    }
}
