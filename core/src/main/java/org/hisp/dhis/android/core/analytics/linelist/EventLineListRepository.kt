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

import io.reactivex.Single
import org.hisp.dhis.android.core.analytics.AnalyticsLegendStrategy
import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EqFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.OrganisationUnitFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.PeriodsFilterConnector

interface EventLineListRepository : BaseRepository {

    /**
     * Restrict the events to the given tracked entity instance.
     */
    fun byTrackedEntityInstance(): EqFilterConnector<EventLineListRepository, String>

    /**
     * Restrict the events to the given program stage. This parameter is mandatory.
     */
    fun byProgramStage(): EqFilterConnector<EventLineListRepository, String>

    /**
     * Restrict the events to the given periods.
     */
    fun byEventDate(): PeriodsFilterConnector<EventLineListRepository>

    /**
     * Restrict the events to the given organisation units.
     */
    fun byOrganisationUnit(): OrganisationUnitFilterConnector<EventLineListRepository>

    /**
     * Include the given data element in the response. This method does not replace the list of
     * data elements but appends a new one to the response.
     */
    fun withDataElement(dataElementUid: String): EventLineListRepository

    /**
     * Include the given program indicator in the response. This method does not replace the list of
     * program indicators but appends a new one to the response.
     */
    fun withProgramIndicator(programIndicatorUid: String): EventLineListRepository

    /**
     * Assign the strategy to apply with legend.
     */
    fun withLegendStrategy(analyticsLegendStrategy: AnalyticsLegendStrategy): EventLineListRepository

    /**
     * Evaluate the given parameters and get a list of events in the format of [LineListResponse].
     *
     * It is mandatory to specify a programStage using the method [byProgramStage]. Other parameters are optional.
     */
    fun evaluate(): Single<List<LineListResponse>>

    /**
     * Blocking version of [evaluate].
     *
     * @see evaluate
     */
    fun blockingEvaluate(): List<LineListResponse>
}
