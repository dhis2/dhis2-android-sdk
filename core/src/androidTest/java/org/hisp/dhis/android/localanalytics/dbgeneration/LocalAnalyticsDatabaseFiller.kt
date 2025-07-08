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
package org.hisp.dhis.android.localanalytics.dbgeneration

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.D2DIComponentAccessor
import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.persistence.category.CategoryComboStoreImpl
import org.hisp.dhis.android.persistence.category.CategoryOptionComboStoreImpl
import org.hisp.dhis.android.persistence.dataelement.DataElementStoreImpl
import org.hisp.dhis.android.persistence.datavalue.DataValueStoreImpl
import org.hisp.dhis.android.persistence.enrollment.EnrollmentStoreImpl
import org.hisp.dhis.android.persistence.event.EventStoreImpl
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitStoreImpl
import org.hisp.dhis.android.persistence.program.ProgramStageStoreImpl
import org.hisp.dhis.android.persistence.program.ProgramStoreImpl
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeStoreImpl
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeValueStoreImpl
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityDataValueStoreImpl
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceStoreImpl

internal data class MetadataForDataFilling(
    val organisationUnits: List<OrganisationUnit>,
    val periods: List<Period>,
    val categoryOptionCombos: List<CategoryOptionCombo>,
    val aggregatedDataElements: List<DataElement>,
    val trackerDataElements: List<DataElement>,
    val programs: List<Program>,
    val programStages: List<ProgramStage>,
    val trackedEntityAttributes: List<TrackedEntityAttribute>,
)

internal class LocalAnalyticsDatabaseFiller(private val d2: D2) {
    private val da = d2.databaseAdapter()
    private val d2DIComponent = D2DIComponentAccessor.getD2DIComponent(d2)

    fun fillDatabase(metadataParams: LocalAnalyticsMetadataParams, dataParams: LocalAnalyticsDataParams) = runTest {
        D2CallExecutor.create(da).executeD2CallTransactionally {
            val metadata = fillMetadata(metadataParams)
            fillData(dataParams, metadata)
        }
    }

    private suspend fun fillMetadata(metadataParams: LocalAnalyticsMetadataParams): MetadataForDataFilling {
        val generator = LocalAnalyticsMetadataGenerator(metadataParams)

        val organisationUnits = generator.getOrganisationUnits()
        OrganisationUnitStoreImpl(da).insert(organisationUnits)

        val categoryCombos = generator.getCategoryCombos()
        CategoryComboStoreImpl(da).insert(categoryCombos)

        val categoryOptionCombos = generator.getCategoryOptionCombos(categoryCombos)
        CategoryOptionComboStoreImpl(da).insert(categoryOptionCombos)

        val defaultCategoryCombo = categoryCombos.first()
        val aggregatedDataElements = generator.getDataElementsAggregated(categoryCombos)
        val trackerDataElements = generator.getDataElementsTracker(defaultCategoryCombo)
        DataElementStoreImpl(da).insert(aggregatedDataElements + trackerDataElements)

        d2DIComponent.periodHandler.generateAndPersist()

        val programs = generator.getPrograms(defaultCategoryCombo)
        ProgramStoreImpl(da).insert(programs)

        val programStages = generator.getProgramStages(programs)
        ProgramStageStoreImpl(da).insert(programStages)

        val trackedEntityAttributes = generator.getTrackedEntityAttributes()
        TrackedEntityAttributeStoreImpl(da).insert(trackedEntityAttributes)

        val periods = d2.periodModule().periods().byPeriodType().eq(PeriodType.Daily).blockingGet()

        return MetadataForDataFilling(
            organisationUnits,
            periods,
            categoryOptionCombos,
            aggregatedDataElements,
            trackerDataElements,
            programs,
            programStages,
            trackedEntityAttributes,
        )
    }

    private suspend fun fillData(dataParams: LocalAnalyticsDataParams, metadata: MetadataForDataFilling) {
        val generator = LocalAnalyticsDataGenerator(dataParams)

        val dv = generator.generateDataValues(metadata)
        DataValueStoreImpl(da).insert(dv)

        val teis = generator.generateTrackedEntityInstances(metadata.organisationUnits)
        TrackedEntityInstanceStoreImpl(da).insert(teis)

        val enrollments = generator.generateEnrollments(teis, metadata.programs.first())
        EnrollmentStoreImpl(da).insert(enrollments)

        val events = generator.generateEventsWithoutRegistration(metadata) +
            generator.generateEventsRegistration(metadata, enrollments)
        EventStoreImpl(da).insert(events)

        TrackedEntityAttributeValueStoreImpl(da).insert(
            generator.generateTrackedEntityAttributeValues(metadata.trackedEntityAttributes, teis),
        )

        TrackedEntityDataValueStoreImpl(da).insert(
            generator.generateTrackedEntityDataValues(metadata.trackerDataElements, events),
        )
    }
}
