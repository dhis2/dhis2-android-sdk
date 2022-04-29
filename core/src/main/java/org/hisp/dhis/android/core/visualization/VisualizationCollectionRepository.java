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
package org.hisp.dhis.android.core.visualization;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.visualization.VisualizationTableInfo.Columns;
import org.hisp.dhis.android.core.visualization.internal.VisualizationFields;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class VisualizationCollectionRepository
        extends ReadOnlyIdentifiableCollectionRepositoryImpl<Visualization, VisualizationCollectionRepository> {

    @Inject
    VisualizationCollectionRepository(final IdentifiableObjectStore<Visualization> store,
                                      final Map<String, ChildrenAppender<Visualization>> childrenAppenders,
                                      final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new VisualizationCollectionRepository(store, childrenAppenders, s)));
    }

    public StringFilterConnector<VisualizationCollectionRepository> byDescription() {
        return cf.string(Columns.DESCRIPTION);
    }

    public StringFilterConnector<VisualizationCollectionRepository> byDisplayDescription() {
        return cf.string(Columns.DISPLAY_DESCRIPTION);
    }

    public StringFilterConnector<VisualizationCollectionRepository> byDisplayFormName() {
        return cf.string(Columns.DISPLAY_FORM_NAME);
    }

    public StringFilterConnector<VisualizationCollectionRepository> byTitle() {
        return cf.string(Columns.TITLE);
    }

    public StringFilterConnector<VisualizationCollectionRepository> byDisplayTitle() {
        return cf.string(Columns.DISPLAY_TITLE);
    }

    public StringFilterConnector<VisualizationCollectionRepository> bySubtitle() {
        return cf.string(Columns.SUBTITLE);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> byLegendShowKey() {
        return cf.bool(Columns.LEGEND_SHOW_KEY);
    }

    public StringFilterConnector<VisualizationCollectionRepository> byLegendStrategy() {
        return cf.string(Columns.LEGEND_STRATEGY);
    }

    public StringFilterConnector<VisualizationCollectionRepository> byLegendStyle() {
        return cf.string(Columns.LEGEND_STYLE);
    }

    public StringFilterConnector<VisualizationCollectionRepository> byLegendUid() {
        return cf.string(Columns.LEGEND_SET_ID);
    }

    public StringFilterConnector<VisualizationCollectionRepository> byDisplaySubtitle() {
        return cf.string(Columns.DISPLAY_SUBTITLE);
    }

    public EnumFilterConnector<VisualizationCollectionRepository, VisualizationType> byType() {
        return cf.enumC(Columns.TYPE);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> byHideTitle() {
        return cf.bool(Columns.HIDE_TITLE);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> byHideSubtitle() {
        return cf.bool(Columns.HIDE_SUBTITLE);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> byHideEmptyColumns() {
        return cf.bool(Columns.HIDE_EMPTY_COLUMNS);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> byHideEmptyRows() {
        return cf.bool(Columns.HIDE_EMPTY_ROWS);
    }

    public EnumFilterConnector<VisualizationCollectionRepository, HideEmptyItemStrategy> byHideEmptyRowItems() {
        return cf.enumC(Columns.HIDE_EMPTY_ROW_ITEMS);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> byHideLegend() {
        return cf.bool(Columns.HIDE_LEGEND);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> byShowHierarchy() {
        return cf.bool(Columns.SHOW_HIERARCHY);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> byRowTotals() {
        return cf.bool(Columns.ROW_TOTALS);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> byRowSubTotals() {
        return cf.bool(Columns.ROW_SUB_TOTALS);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> byColTotals() {
        return cf.bool(Columns.COL_TOTALS);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> byColSubTotals() {
        return cf.bool(Columns.COL_SUB_TOTALS);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> byShowDimensionLabels() {
        return cf.bool(Columns.SHOW_DIMENSION_LABELS);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> byPercentStackedValues() {
        return cf.bool(Columns.PERCENT_STACKED_VALUES);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> byNoSpaceBetweenColumns() {
        return cf.bool(Columns.NO_SPACE_BETWEEN_COLUMNS);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> bySkipRounding() {
        return cf.bool(Columns.SKIP_ROUNDING);
    }

    public EnumFilterConnector<VisualizationCollectionRepository, DisplayDensity> byDisplayDensity() {
        return cf.enumC(Columns.DISPLAY_DENSITY);
    }

    public EnumFilterConnector<VisualizationCollectionRepository, DigitGroupSeparator> byDigitGroupSeparator() {
        return cf.enumC(Columns.DIGIT_GROUP_SEPARATOR);
    }

    public StringFilterConnector<VisualizationCollectionRepository> byRelativePeriods() {
        return cf.string(Columns.RELATIVE_PERIODS);
    }

    public StringFilterConnector<VisualizationCollectionRepository> byFilterDimensions() {
        return cf.string(Columns.FILTER_DIMENSIONS);
    }

    public StringFilterConnector<VisualizationCollectionRepository> byRowDimensions() {
        return cf.string(Columns.ROW_DIMENSIONS);
    }

    public StringFilterConnector<VisualizationCollectionRepository> byColumnDimensions() {
        return cf.string(Columns.COLUMN_DIMENSIONS);
    }

    public IntegerFilterConnector<VisualizationCollectionRepository> byOrganisationUnitLevels() {
        return cf.integer(Columns.ORGANISATION_UNIT_LEVELS);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> byUserOrganisationUnit() {
        return cf.bool(Columns.USER_ORGANISATION_UNIT);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> byUserOrganisationUnitChildren() {
        return cf.bool(Columns.USER_ORGANISATION_UNIT_CHILDREN);
    }

    public BooleanFilterConnector<VisualizationCollectionRepository> byUserOrganisationUnitGrandChildren() {
        return cf.bool(Columns.USER_ORGANISATION_UNIT_GRAND_CHILDREN);
    }

    public VisualizationCollectionRepository withCategoryDimensions() {
        return cf.withChild(VisualizationFields.CATEGORY_DIMENSIONS);
    }

    public VisualizationCollectionRepository withDataDimensionItems() {
        return cf.withChild(VisualizationFields.DATA_DIMENSION_ITEMS);
    }
}
