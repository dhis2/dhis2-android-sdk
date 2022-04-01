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
package org.hisp.dhis.android.core.analytics.linelist

import javax.inject.Inject
import org.hisp.dhis.android.core.analytics.AnalyticsLegendStrategy
import org.hisp.dhis.android.core.analytics.LegendEvaluator
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsOrganisationUnitHelper
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.common.OrganisationUnitFilter
import org.hisp.dhis.android.core.dataelement.DataElementCollectionRepository
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventCollectionRepository
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.internal.PeriodHelper
import org.hisp.dhis.android.core.program.ProgramIndicatorCollectionRepository
import org.hisp.dhis.android.core.program.ProgramStageCollectionRepository
import org.hisp.dhis.android.core.program.programindicatorengine.ProgramIndicatorEngine
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueCollectionRepository

@Suppress("LongParameterList")
internal class EventLineListServiceImpl @Inject constructor(
    private val eventRepository: EventCollectionRepository,
    private val dataValueRepository: TrackedEntityDataValueCollectionRepository,
    private val dataElementRepository: DataElementCollectionRepository,
    private val programIndicatorRepository: ProgramIndicatorCollectionRepository,
    private val organisationUnitRepository: OrganisationUnitCollectionRepository,
    private val programStageRepository: ProgramStageCollectionRepository,
    private val programIndicatorEngine: ProgramIndicatorEngine,
    private val periodHelper: PeriodHelper,
    private val dateFilterPeriodHelper: DateFilterPeriodHelper,
    private val organisationUnitHelper: AnalyticsOrganisationUnitHelper,
    private val legendEvaluator: LegendEvaluator
) : EventLineListService {

    override fun evaluate(params: EventLineListParams): List<LineListResponse> {
        return evaluateEvents(params)
    }

    @Suppress("LongMethod", "ComplexMethod")
    private fun evaluateEvents(params: EventLineListParams): List<LineListResponse> {

        val events = getEvents(params)
        val programStage = programStageRepository.uid(params.programStage).blockingGet()

        val metadataMap = getMetadataMap(
            params.dataElements, params.programIndicators,
            events.map { it.organisationUnit()!! }.toHashSet()
        )

        val dataElementValues = if (params.dataElements.isNotEmpty()) {
            dataValueRepository
                .byEvent().`in`(events.map { it.uid() })
                .byDataElement().`in`(params.dataElements.map { it.uid })
                .blockingGet()
        } else {
            listOf()
        }

        return events.mapNotNull {
            (it.eventDate() ?: it.dueDate())?.let { referenceDate ->
                val periodType = programStage.periodType() ?: PeriodType.Daily
                val eventPeriod = periodHelper.blockingGetPeriodForPeriodTypeAndDate(periodType, referenceDate)

                val eventDataValues = params.dataElements.map { de ->
                    val dv = dataElementValues.find { dv -> dv.event() == it.uid() && dv.dataElement() == de.uid }

                    val legend = when (params.analyticsLegendStrategy) {
                        is AnalyticsLegendStrategy.None -> null
                        is AnalyticsLegendStrategy.ByDataItem -> legendEvaluator.getLegendByDataElement(
                            de.uid, dv?.value()
                        )
                        is AnalyticsLegendStrategy.Fixed -> legendEvaluator.getLegendByLegendSet(
                            params.analyticsLegendStrategy.legendSetUid,
                            dv?.value()
                        )
                    }

                    LineListResponseValue(
                        uid = de.uid,
                        displayName = metadataMap[de.uid] ?: de.uid,
                        value = dv?.value(),
                        legend = legend
                    )
                }

                val programIndicatorValues = params.programIndicators.map { pi ->

                    val value = programIndicatorEngine.getEventProgramIndicatorValue(it.uid(), pi.uid)

                    val legend = when (params.analyticsLegendStrategy) {
                        is AnalyticsLegendStrategy.None -> null
                        is AnalyticsLegendStrategy.ByDataItem -> legendEvaluator.getLegendByProgramIndicator(
                            pi.uid, value
                        )
                        is AnalyticsLegendStrategy.Fixed -> legendEvaluator.getLegendByLegendSet(
                            params.analyticsLegendStrategy.legendSetUid, value
                        )
                    }

                    LineListResponseValue(
                        uid = pi.uid,
                        displayName = metadataMap[pi.uid] ?: pi.uid,
                        value = value,
                        legend = legend
                    )
                }

                LineListResponse(
                    uid = it.uid(),
                    date = referenceDate,
                    period = eventPeriod,
                    organisationUnit = it.organisationUnit()!!,
                    organisationUnitName = metadataMap[it.organisationUnit()!!] ?: it.organisationUnit()!!,
                    values = eventDataValues + programIndicatorValues
                )
            }
        }
    }

    private fun getEvents(params: EventLineListParams): List<Event> {
        var repoBuilder = eventRepository
            .byProgramStageUid().eq(params.programStage)
            .orderByTimeline(RepositoryScope.OrderByDirection.ASC)
            .byDeleted().isFalse

        if (params.trackedEntityInstance != null) {
            repoBuilder = repoBuilder.byTrackedEntityInstanceUids(listOf(params.trackedEntityInstance))
        }

        getOrganisationUnitUids(params.organisationUnits)?.let {
            repoBuilder = repoBuilder.byOrganisationUnitUid().`in`(it)
        }

        return if (params.eventDates.isNullOrEmpty()) {
            repoBuilder.blockingGet()
        } else {
            params.eventDates.flatMap { filter ->
                var innerBuilder = repoBuilder
                dateFilterPeriodHelper.getStartDate(filter)?.let {
                    innerBuilder = innerBuilder.byEventDate().afterOrEqual(it)
                }
                dateFilterPeriodHelper.getEndDate(filter)?.let {
                    innerBuilder = innerBuilder.byEventDate().beforeOrEqual(it)
                }
                innerBuilder.blockingGet()
            }
        }
    }

    private fun getMetadataMap(
        dataElements: List<LineListItem>,
        programIndicators: List<LineListItem>,
        organisationUnitUids: HashSet<String>
    ): Map<String, String> {
        val dataElementNameMap = if (dataElements.isNotEmpty()) {
            dataElementRepository
                .byUid().`in`(dataElements.map { it.uid })
                .blockingGet()
                .map { it.uid()!! to it.displayName()!! }.toMap()
        } else {
            mapOf()
        }

        val programIndicatorNameMap = if (programIndicators.isNotEmpty()) {
            programIndicatorRepository
                .byUid().`in`(programIndicators.map { it.uid })
                .blockingGet()
                .map { it.uid()!! to it.displayName()!! }.toMap()
        } else {
            mapOf()
        }

        val organisationUnitNameMap = organisationUnitRepository
            .byUid().`in`(organisationUnitUids)
            .blockingGet()
            .map { it.uid()!! to it.displayName()!! }.toMap()

        return dataElementNameMap + programIndicatorNameMap + organisationUnitNameMap
    }

    private fun getOrganisationUnitUids(organisationUnitFilters: List<OrganisationUnitFilter>?): List<String>? {
        return organisationUnitFilters?.flatMap { filter ->
            val relativeOrgunitUids = filter.relativeOrganisationUnit?.let {
                organisationUnitHelper.getRelativeOrganisationUnitUids(it)
            } ?: listOf()

            relativeOrgunitUids + filter.organisationUnitUid
        }?.filterNotNull()
    }
}
