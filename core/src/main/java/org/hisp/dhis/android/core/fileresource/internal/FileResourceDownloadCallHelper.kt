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

import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboCategoryOptionLinkStore
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.dataset.internal.DataSetElementStore
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.fileresource.FileResourceValueType
import org.hisp.dhis.android.core.icon.CustomIcon
import org.hisp.dhis.android.core.icon.internal.CustomIconStore
import org.hisp.dhis.android.core.program.internal.ProgramTrackedEntityAttributeStore
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.internal.DHISVersionManagerImpl
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
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
import org.hisp.dhis.android.persistence.program.ProgramTrackedEntityAttributeTableInfo
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
    private val programTrackedEntityAttributeStore: ProgramTrackedEntityAttributeStore,
    private val enrollmentStore: EnrollmentStore,
    private val eventStore: EventStore,
    private val dataSetElementStore: DataSetElementStore,
    private val dataValueStore: DataValueStore,
    private val customIconStore: CustomIconStore,
    private val categoryOptionComboStore: CategoryOptionComboStore,
    private val categoryOptionComboCategoryOptionLinkStore: CategoryOptionComboCategoryOptionLinkStore,
    private val dhisVersionManager: DHISVersionManagerImpl,
) {

    suspend fun getMissingTrackerAttributeValues(
        params: FileResourceDownloadParams,
        existingFileResources: List<String>,
    ): List<MissingTrackerAttributeValue> {
        val fileTypes = params.valueTypes.filter { type ->
            dhisVersionManager.isGreaterOrEqualThanInternal(DHISVersion.V2_40) || type == FileResourceValueType.IMAGE
        }

        val attributesWhereClause = WhereClauseBuilder()
            .appendInKeyEnumValues(TrackedEntityAttributeTableInfo.Columns.VALUE_TYPE, fileTypes.map { it.valueType })
            .build()

        val trackedEntityAttributes = trackedEntityAttributeStore.selectWhere(attributesWhereClause)

        val attributeValuesWhereClauseBuilder = WhereClauseBuilder().apply {
            val attributes = trackedEntityAttributes.map { it.uid() }
            appendInKeyStringValues(TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE, attributes)
            appendNotInKeyStringValues(TrackedEntityAttributeValueTableInfo.Columns.VALUE, existingFileResources)

            if (params.trackedEntityAttributeUids.isNotEmpty()) {
                appendInKeyStringValues(
                    TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE,
                    params.trackedEntityAttributeUids,
                )
            }

            val teiParamsClause = getTrackedEntityWhereClauseFromParams(params)

            if (teiParamsClause.isEmpty.not()) {
                val teis = trackedEntityInstanceStore.selectUidsWhere(teiParamsClause.build())
                appendInKeyStringValues(TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE, teis)
            }
        }

        val attributeValues = trackedEntityAttributeValueStore.selectWhere(attributeValuesWhereClauseBuilder.build())
        val resolveProgram = buildAttributeProgramResolver(attributeValues, params.programUids.singleOrNull())

        return attributeValues.map { av ->
            val type = trackedEntityAttributes.find { it.uid() == av.trackedEntityAttribute() }!!.valueType()!!
            MissingTrackerAttributeValue(av, type, resolveProgram(av))
        }
    }

    /**
     * Builds a function that resolves the program to send when downloading the file of an attribute value. The
     * tracker API requires it for attributes assigned to a program: without it the file is not resolved.
     *
     * The metadata both decisions are based on is read once for the whole batch, not once per value.
     */
    private suspend fun buildAttributeProgramResolver(
        attributeValues: List<TrackedEntityAttributeValue>,
        contextProgramUid: String?,
    ): (TrackedEntityAttributeValue) -> String? {
        if (attributeValues.isEmpty()) {
            return { null }
        }

        val programsByAttribute = getProgramsByAttribute(attributeValues)
        val programsByTrackedEntity = getProgramsByTrackedEntity(attributeValues)

        return { value ->
            resolveAttributeProgram(
                programsWithAttribute = programsByAttribute[value.trackedEntityAttribute()].orEmpty(),
                programsOfTrackedEntity = programsByTrackedEntity[value.trackedEntityInstance()].orEmpty(),
                contextProgramUid = contextProgramUid,
            )
        }
    }

    /**
     * The program the download is scoped to when the attribute belongs to it, otherwise a program the attribute is
     * assigned to and the tracked entity is enrolled in. Null for attributes not assigned to any program.
     */
    private fun resolveAttributeProgram(
        programsWithAttribute: List<String>,
        programsOfTrackedEntity: Set<String>,
        contextProgramUid: String?,
    ): String? {
        return when {
            programsWithAttribute.isEmpty() -> null
            contextProgramUid in programsWithAttribute -> contextProgramUid
            else -> programsWithAttribute.firstOrNull { it in programsOfTrackedEntity }
                ?: programsWithAttribute.first()
        }
    }

    private suspend fun getProgramsByAttribute(
        attributeValues: List<TrackedEntityAttributeValue>,
    ): Map<String?, List<String>> {
        val whereClause = WhereClauseBuilder()
            .appendInKeyStringValues(
                ProgramTrackedEntityAttributeTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE,
                attributeValues.map { it.trackedEntityAttribute() }.distinct(),
            )
            .build()

        return programTrackedEntityAttributeStore.selectWhere(whereClause)
            .groupBy({ it.trackedEntityAttribute()?.uid() }, { it.program()?.uid() })
            .mapValues { (_, programs) -> programs.filterNotNull().distinct().sorted() }
    }

    private suspend fun getProgramsByTrackedEntity(
        attributeValues: List<TrackedEntityAttributeValue>,
    ): Map<String?, Set<String>> {
        val whereClause = WhereClauseBuilder()
            .appendInKeyStringValues(
                EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                attributeValues.map { it.trackedEntityInstance() }.distinct(),
            )
            .build()

        return enrollmentStore.selectWhere(whereClause)
            .groupBy({ it.trackedEntityInstance() }, { it.program() })
            .mapValues { (_, programs) -> programs.filterNotNull().toSet() }
    }

    suspend fun getMissingTrackerDataValues(
        params: FileResourceDownloadParams,
        existingFileResources: List<String>,
    ): List<MissingTrackerDataValue> {
        val dataElements = dataElementStore.selectWhere(
            WhereClauseBuilder()
                .appendInKeyEnumValues(DataElementTableInfo.Columns.VALUE_TYPE, params.valueTypes.map { it.valueType })
                .appendKeyStringValue(DataElementTableInfo.Columns.DOMAIN_TYPE, "TRACKER")
                .build(),
        )

        val dataValuesWhereClause = WhereClauseBuilder().apply {
            val dataElementUids = dataElements.map { it.uid() }
            appendInKeyStringValues(TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT, dataElementUids)
            appendNotInKeyStringValues(TrackedEntityDataValueTableInfo.Columns.VALUE, existingFileResources)

            val eventWhereClause = getEventWhereClauseFromParams(params)

            if (eventWhereClause.isEmpty.not()) {
                val eventUids = eventStore.selectUidsWhere(eventWhereClause.build())
                appendInKeyStringValues(TrackedEntityDataValueTableInfo.Columns.EVENT, eventUids)
            }
        }.build()

        return trackedEntityDataValueStore.selectWhere(dataValuesWhereClause)
            .map { dv ->
                val type = dataElements.find { it.uid() == dv.dataElement() }!!.valueType()!!
                MissingTrackerDataValue(dv, type)
            }
    }

    suspend fun getMissingAggregatedDataValues(
        params: FileResourceDownloadParams,
        existingFileResources: List<String>,
    ): List<MissingAggregatedDataValue> {
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

        val dataValues = dataValueStore.selectWhere(dataValuesWhereClause)
        val attributeOptionCombos = dataValues
            .map { it.attributeOptionCombo() }
            .distinct()
            .associateWith { getAttributeOptionComboParams(it) }

        return dataValues.map { dataValue ->
            val (categoryCombo, categoryOptions) = attributeOptionCombos.getValue(dataValue.attributeOptionCombo())
            MissingAggregatedDataValue(dataValue, categoryCombo, categoryOptions)
        }
    }

    private suspend fun getAttributeOptionComboParams(attributeOptionCombo: String): Pair<String?, String?> {
        val categoryCombo = categoryOptionComboStore.selectByUid(attributeOptionCombo)?.categoryCombo()?.uid()

        val categoryOptions = categoryOptionComboCategoryOptionLinkStore
            .selectLinksForMasterUid(attributeOptionCombo)
            .map { it.categoryOption() }
            .takeIf { it.isNotEmpty() }
            ?.let { CollectionsHelper.semicolonSeparatedCollectionValues(it) }

        return categoryCombo to categoryOptions
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
