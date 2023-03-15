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
package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator

import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.indicatorengine.IndicatorEngine
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStoreImpl
import org.hisp.dhis.android.core.constant.internal.ConstantStore
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitGroupStore
import org.hisp.dhis.android.core.parser.internal.service.ExpressionService
import org.hisp.dhis.android.core.program.internal.ProgramStageStore
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLExecutor
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
internal class IndicatorEvaluatorIntegrationShould : IndicatorEvaluatorIntegrationBaseShould() {

    private val dataElementEvaluator = DataElementSQLEvaluator(databaseAdapter)
    private val programIndicatorExecutor = ProgramIndicatorSQLExecutor(
        ConstantStore.create(databaseAdapter),
        DataElementStore.create(databaseAdapter),
        TrackedEntityAttributeStore.create(databaseAdapter),
        databaseAdapter
    )
    private val programIndicatorEvaluator = ProgramIndicatorSQLEvaluator(
        programIndicatorExecutor
    )
    private val eventDataItemEvaluator = EventDataItemSQLEvaluator(databaseAdapter)

    private val expressionService = ExpressionService(
        DataElementStore.create(databaseAdapter),
        CategoryOptionComboStoreImpl.create(databaseAdapter),
        OrganisationUnitGroupStore.create(databaseAdapter),
        ProgramStageStore.create(databaseAdapter)
    )

    private val indicatorEngine = IndicatorEngine(
        indicatorTypeStore,
        DataElementStore.create(databaseAdapter),
        TrackedEntityAttributeStore.create(databaseAdapter),
        CategoryOptionComboStoreImpl.create(databaseAdapter),
        ProgramStore.create(databaseAdapter),
        d2.programModule().programIndicators(),
        dataElementEvaluator,
        programIndicatorEvaluator,
        eventDataItemEvaluator,
        ConstantStore.create(databaseAdapter),
        expressionService
    )

    override val indicatorEvaluator = IndicatorEvaluator(indicatorEngine)
}
