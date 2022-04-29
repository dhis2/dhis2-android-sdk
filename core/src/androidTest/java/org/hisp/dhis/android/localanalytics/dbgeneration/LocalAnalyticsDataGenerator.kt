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

import java.util.*
import kotlin.random.Random
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl
import org.hisp.dhis.android.core.data.datavalue.DataValueSamples
import org.hisp.dhis.android.core.data.enrollment.EnrollmentSamples
import org.hisp.dhis.android.core.data.trackedentity.EventSamples
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityAttributeValueSamples
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityDataValueSamples
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityInstanceSamples
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance

internal class LocalAnalyticsDataGenerator(private val params: LocalAnalyticsDataParams) {

    private val random = Random(132214235)
    private val uidGenerator = UidGeneratorImpl()

    fun generateDataValues(metadata: MetadataForDataFilling): List<DataValue> {
        val orgUnits: List<OrganisationUnit> = metadata.organisationUnits.filter { it.level() == 3 }
            .groupBy { it.parent() }.map { aa -> aa.value.first() }
        val categoryOptionCombosByCategoryCombo = metadata.categoryOptionCombos
            .groupBy { coc -> coc.categoryCombo() }
        val dataElementsByCategoryCombo = metadata.aggregatedDataElements
            .groupBy { de -> de.categoryCombo() }

        val periodOrgUnits = metadata.periods.flatMap {
            period ->
            orgUnits.map { ou -> Pair(period, ou) }
        }

        val iterations = params.dataValues / 100

        return categoryOptionCombosByCategoryCombo.flatMap {
            (categoryCombo, categoryOptionCombos) ->
            categoryOptionCombos.flatMap { categoryOptionCombo ->
                dataElementsByCategoryCombo[categoryCombo]!!.flatMap { dataElement ->
                    (0 until iterations).map {
                        val (period, ou) = periodOrgUnits[it]
                        DataValueSamples.getDataValue(
                            ou.uid(), dataElement.uid(), period.periodId()!!, categoryOptionCombo.uid(),
                            metadata.categoryOptionCombos.first().uid(), random.nextDouble().toString()
                        )
                    }
                }
            }
        }
    }

    fun generateTrackedEntityInstances(organisationUnits: List<OrganisationUnit>): List<TrackedEntityInstance> {
        val level3OrgUnits = organisationUnits.filter { ou -> ou.level() == 3 }
        return (1..params.trackedEntityInstances).map { i ->
            val ou = level3OrgUnits[i % level3OrgUnits.size]
            TrackedEntityInstanceSamples.get(ou.uid())
        }
    }

    fun generateEnrollments(teis: List<TrackedEntityInstance>, program: Program): List<Enrollment> {
        return teis.map { tei ->
            EnrollmentSamples.get(
                uidGenerator.generate(), tei.organisationUnit(), program.uid(), tei.uid(),
                getRandomDateInLastYear()
            )
        }
    }

    fun generateEventsWithoutRegistration(metadata: MetadataForDataFilling): List<Event> {
        val level3OrgUnits = metadata.organisationUnits.filter { ou -> ou.level() == 3 }
        val program = metadata.programs[1]
        val programStages = metadata.programStages
            .filter { ps -> ps.program()!!.uid() == program.uid() }
        return (1..params.eventsWithoutRegistration).map { i ->
            val ou = level3OrgUnits[i % level3OrgUnits.size]
            val programStage = programStages[i % programStages.size]
            EventSamples.get(
                uidGenerator.generate(), null, ou.uid(), programStage.program()!!.uid(), programStage.uid(),
                metadata.categoryOptionCombos.first().uid(), getRandomDateInLastYear()
            )
        }
    }

    fun generateEventsRegistration(metadata: MetadataForDataFilling, enrollments: List<Enrollment>): List<Event> {
        val program = metadata.programs[0]
        val programStages = metadata.programStages
            .filter { ps -> ps.program()!!.uid() == program.uid() }
        return enrollments.flatMap { enrollment ->
            programStages.flatMap { ps ->
                (1..params.eventsWithRegistrationPerEnrollmentAndPS).map {
                    EventSamples.get(
                        uidGenerator.generate(), enrollment.uid(), enrollment.organisationUnit(),
                        enrollment.program(), ps.uid(), metadata.categoryOptionCombos.first().uid(),
                        getRandomDateInLastYear()
                    )
                }
            }
        }
    }

    fun generateTrackedEntityAttributeValues(
        trackedEntityAttributes: List<TrackedEntityAttribute>,
        teis: List<TrackedEntityInstance>
    ): List<TrackedEntityAttributeValue> {
        return trackedEntityAttributes.flatMap { tea ->
            teis.map { tei ->
                TrackedEntityAttributeValueSamples.get(tea.uid(), tei.uid(), generateRandomStringValue())
            }
        }
    }

    fun generateTrackedEntityDataValues(
        dataElements: List<DataElement>,
        events: List<Event>
    ): List<TrackedEntityDataValue> {
        return dataElements.flatMap { de ->
            events.map { event ->
                TrackedEntityDataValueSamples.get(de.uid(), event.uid(), generateRandomStringValue())
            }
        }
    }

    private fun generateRandomStringValue(): String {
        return uidGenerator.generate()
    }

    private fun getRandomDateInLastYear(): Date {
        val now = System.currentTimeMillis()
        val oneYearMillis = 365L * 24 * 60 * 60 * 1000
        val millis = now - random.nextDouble() * oneYearMillis
        return Date(millis.toLong())
    }
}
