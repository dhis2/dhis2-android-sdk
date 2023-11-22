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
package org.hisp.dhis.android.core.visualization

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.visualization.internal.VisualizationColumnsRowsFiltersChildrenAppender
import org.hisp.dhis.android.core.visualization.internal.VisualizationFields
import org.hisp.dhis.android.core.visualization.internal.VisualizationStore
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
class VisualizationCollectionRepository internal constructor(
    store: VisualizationStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyIdentifiableCollectionRepositoryImpl<Visualization, VisualizationCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        VisualizationCollectionRepository(
            store,
            databaseAdapter,
            s,
        )
    },
) {
    fun byDescription(): StringFilterConnector<VisualizationCollectionRepository> {
        return cf.string(VisualizationTableInfo.Columns.DESCRIPTION)
    }

    fun byDisplayDescription(): StringFilterConnector<VisualizationCollectionRepository> {
        return cf.string(VisualizationTableInfo.Columns.DISPLAY_DESCRIPTION)
    }

    fun byDisplayFormName(): StringFilterConnector<VisualizationCollectionRepository> {
        return cf.string(VisualizationTableInfo.Columns.DISPLAY_FORM_NAME)
    }

    fun byTitle(): StringFilterConnector<VisualizationCollectionRepository> {
        return cf.string(VisualizationTableInfo.Columns.TITLE)
    }

    fun byDisplayTitle(): StringFilterConnector<VisualizationCollectionRepository> {
        return cf.string(VisualizationTableInfo.Columns.DISPLAY_TITLE)
    }

    fun bySubtitle(): StringFilterConnector<VisualizationCollectionRepository> {
        return cf.string(VisualizationTableInfo.Columns.SUBTITLE)
    }

    fun byLegendShowKey(): BooleanFilterConnector<VisualizationCollectionRepository> {
        return cf.bool(VisualizationTableInfo.Columns.LEGEND_SHOW_KEY)
    }

    fun byLegendStrategy(): StringFilterConnector<VisualizationCollectionRepository> {
        return cf.string(VisualizationTableInfo.Columns.LEGEND_STRATEGY)
    }

    fun byLegendStyle(): StringFilterConnector<VisualizationCollectionRepository> {
        return cf.string(VisualizationTableInfo.Columns.LEGEND_STYLE)
    }

    fun byLegendUid(): StringFilterConnector<VisualizationCollectionRepository> {
        return cf.string(VisualizationTableInfo.Columns.LEGEND_SET_ID)
    }

    fun byDisplaySubtitle(): StringFilterConnector<VisualizationCollectionRepository> {
        return cf.string(VisualizationTableInfo.Columns.DISPLAY_SUBTITLE)
    }

    fun byType(): EnumFilterConnector<VisualizationCollectionRepository, VisualizationType> {
        return cf.enumC(VisualizationTableInfo.Columns.TYPE)
    }

    fun byHideTitle(): BooleanFilterConnector<VisualizationCollectionRepository> {
        return cf.bool(VisualizationTableInfo.Columns.HIDE_TITLE)
    }

    fun byHideSubtitle(): BooleanFilterConnector<VisualizationCollectionRepository> {
        return cf.bool(VisualizationTableInfo.Columns.HIDE_SUBTITLE)
    }

    fun byHideEmptyColumns(): BooleanFilterConnector<VisualizationCollectionRepository> {
        return cf.bool(VisualizationTableInfo.Columns.HIDE_EMPTY_COLUMNS)
    }

    fun byHideEmptyRows(): BooleanFilterConnector<VisualizationCollectionRepository> {
        return cf.bool(VisualizationTableInfo.Columns.HIDE_EMPTY_ROWS)
    }

    fun byHideEmptyRowItems(): EnumFilterConnector<VisualizationCollectionRepository, HideEmptyItemStrategy> {
        return cf.enumC(VisualizationTableInfo.Columns.HIDE_EMPTY_ROW_ITEMS)
    }

    fun byHideLegend(): BooleanFilterConnector<VisualizationCollectionRepository> {
        return cf.bool(VisualizationTableInfo.Columns.HIDE_LEGEND)
    }

    fun byShowHierarchy(): BooleanFilterConnector<VisualizationCollectionRepository> {
        return cf.bool(VisualizationTableInfo.Columns.SHOW_HIERARCHY)
    }

    fun byRowTotals(): BooleanFilterConnector<VisualizationCollectionRepository> {
        return cf.bool(VisualizationTableInfo.Columns.ROW_TOTALS)
    }

    fun byRowSubTotals(): BooleanFilterConnector<VisualizationCollectionRepository> {
        return cf.bool(VisualizationTableInfo.Columns.ROW_SUB_TOTALS)
    }

    fun byColTotals(): BooleanFilterConnector<VisualizationCollectionRepository> {
        return cf.bool(VisualizationTableInfo.Columns.COL_TOTALS)
    }

    fun byColSubTotals(): BooleanFilterConnector<VisualizationCollectionRepository> {
        return cf.bool(VisualizationTableInfo.Columns.COL_SUB_TOTALS)
    }

    fun byShowDimensionLabels(): BooleanFilterConnector<VisualizationCollectionRepository> {
        return cf.bool(VisualizationTableInfo.Columns.SHOW_DIMENSION_LABELS)
    }

    fun byPercentStackedValues(): BooleanFilterConnector<VisualizationCollectionRepository> {
        return cf.bool(VisualizationTableInfo.Columns.PERCENT_STACKED_VALUES)
    }

    fun byNoSpaceBetweenColumns(): BooleanFilterConnector<VisualizationCollectionRepository> {
        return cf.bool(VisualizationTableInfo.Columns.NO_SPACE_BETWEEN_COLUMNS)
    }

    fun bySkipRounding(): BooleanFilterConnector<VisualizationCollectionRepository> {
        return cf.bool(VisualizationTableInfo.Columns.SKIP_ROUNDING)
    }

    fun byDisplayDensity(): EnumFilterConnector<VisualizationCollectionRepository, DisplayDensity> {
        return cf.enumC(VisualizationTableInfo.Columns.DISPLAY_DENSITY)
    }

    fun byDigitGroupSeparator(): EnumFilterConnector<VisualizationCollectionRepository, DigitGroupSeparator> {
        return cf.enumC(VisualizationTableInfo.Columns.DIGIT_GROUP_SEPARATOR)
    }

    fun withColumnsRowsAndFilters(): VisualizationCollectionRepository {
        return cf.withChild(VisualizationFields.ITEMS)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<Visualization> = mapOf(
            VisualizationFields.ITEMS to VisualizationColumnsRowsFiltersChildrenAppender::create,
        )
    }
}
