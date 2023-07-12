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

package org.hisp.dhis.android.core.parser.internal.expression

import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.indicatorengine.IndicatorContext
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorContext
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorExecutor
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLContext
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute

@Suppress("LongParameterList")
internal sealed class CommonExpressionVisitorScope(
    val constantMap: Map<String, Constant>,
    val itemMap: Map<Int, ExpressionItem>,
    val itemMethod: ExpressionItemMethod,
    val dataElementStore: IdentifiableObjectStore<DataElement>? = null,
    val categoryOptionComboStore: IdentifiableObjectStore<CategoryOptionCombo>? = null,
    val organisationUnitGroupStore: IdentifiableObjectStore<OrganisationUnitGroup>? = null,
    val programStageStore: IdentifiableObjectStore<ProgramStage>? = null,
    val trackedAttributeStore: IdentifiableObjectStore<TrackedEntityAttribute>? = null,
    val indicatorContext: IndicatorContext? = null,
    val programIndicatorContext: ProgramIndicatorContext? = null,
    val programIndicatorSQLContext: ProgramIndicatorSQLContext? = null,
    val programIndicatorExecutor: ProgramIndicatorExecutor? = null
) {
    class Expression(
        constantMap: Map<String, Constant>,
        itemMap: Map<Int, ExpressionItem>,
        itemMethod: ExpressionItemMethod,
        dataElementStore: IdentifiableObjectStore<DataElement>,
        categoryOptionComboStore: IdentifiableObjectStore<CategoryOptionCombo>,
        organisationUnitGroupStore: IdentifiableObjectStore<OrganisationUnitGroup>,
        programStageStore: IdentifiableObjectStore<ProgramStage>,
    ) : CommonExpressionVisitorScope(
        constantMap,
        itemMap,
        itemMethod,
        dataElementStore = dataElementStore,
        categoryOptionComboStore = categoryOptionComboStore,
        organisationUnitGroupStore = organisationUnitGroupStore,
        programStageStore = programStageStore
    )

    class ProgramIndicator(
        constantMap: Map<String, Constant>,
        itemMap: Map<Int, ExpressionItem>,
        itemMethod: ExpressionItemMethod,
        programIndicatorContext: ProgramIndicatorContext,
        programIndicatorExecutor: ProgramIndicatorExecutor,
        dataElementStore: IdentifiableObjectStore<DataElement>,
        trackedEntityAttributeStore: IdentifiableObjectStore<TrackedEntityAttribute>,
        programStageStore: IdentifiableObjectStore<ProgramStage>
    ) : CommonExpressionVisitorScope(
        constantMap,
        itemMap,
        itemMethod,
        programIndicatorContext = programIndicatorContext,
        programIndicatorExecutor = programIndicatorExecutor,
        dataElementStore = dataElementStore,
        trackedAttributeStore = trackedEntityAttributeStore,
        programStageStore = programStageStore
    )

    class ProgramSQLIndicator(
        constantMap: Map<String, Constant>,
        itemMap: Map<Int, ExpressionItem>,
        itemMethod: ExpressionItemMethod,
        programIndicatorSQLContext: ProgramIndicatorSQLContext,
        dataElementStore: IdentifiableObjectStore<DataElement>,
        trackedEntityAttributeStore: IdentifiableObjectStore<TrackedEntityAttribute>
    ) : CommonExpressionVisitorScope(
        constantMap,
        itemMap,
        itemMethod,
        programIndicatorSQLContext = programIndicatorSQLContext,
        dataElementStore = dataElementStore,
        trackedAttributeStore = trackedEntityAttributeStore
    )

    class AnalyticsIndicator(
        constantMap: Map<String, Constant>,
        itemMap: Map<Int, ExpressionItem>,
        itemMethod: ExpressionItemMethod,
        indicatorContext: IndicatorContext
    ) : CommonExpressionVisitorScope(
        constantMap,
        itemMap,
        itemMethod,
        indicatorContext = indicatorContext
    )
}
