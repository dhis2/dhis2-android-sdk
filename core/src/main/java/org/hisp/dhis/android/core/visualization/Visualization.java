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

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbVisualizationLegendColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.IntegerListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.ObjectWithUidListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.RelativePeriodsColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.StringListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.AggregationTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.DigitGroupSeparatorColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.DisplayDensityColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.HideEmptyItemStrategyColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.VisualizationTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreCategoryDimensionListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreDataDimensionItemListColumnAdapter;
import org.hisp.dhis.android.core.common.AggregationType;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.RelativePeriod;

import java.util.List;
import java.util.Map;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_Visualization.Builder.class)
@SuppressWarnings({"PMD.ExcessivePublicCount", "PMD.CouplingBetweenObjects"})
public abstract class Visualization extends BaseIdentifiableObject implements CoreObject {

    @Nullable
    @JsonProperty()
    public abstract String description();

    @Nullable
    @JsonProperty()
    public abstract String displayDescription();

    @Nullable
    @JsonProperty()
    public abstract String displayFormName();

    @Nullable
    @JsonProperty()
    public abstract String title();

    @Nullable
    @JsonProperty()
    public abstract String displayTitle();

    @Nullable
    @JsonProperty()
    public abstract String subtitle();

    @Nullable
    @JsonProperty()
    public abstract String displaySubtitle();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(VisualizationTypeColumnAdapter.class)
    public abstract VisualizationType type();

    @Nullable
    @JsonProperty()
    public abstract Boolean hideTitle();

    @Nullable
    @JsonProperty()
    public abstract Boolean hideSubtitle();

    @Nullable
    @JsonProperty()
    public abstract Boolean hideEmptyColumns();

    @Nullable
    @JsonProperty()
    public abstract Boolean hideEmptyRows();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(HideEmptyItemStrategyColumnAdapter.class)
    public abstract HideEmptyItemStrategy hideEmptyRowItems();

    @Nullable
    @JsonProperty()
    public abstract Boolean hideLegend();

    @Nullable
    @JsonProperty()
    public abstract Boolean showHierarchy();

    @Nullable
    @JsonProperty()
    public abstract Boolean rowTotals();

    @Nullable
    @JsonProperty()
    public abstract Boolean rowSubTotals();

    @Nullable
    @JsonProperty()
    public abstract Boolean colTotals();

    @Nullable
    @JsonProperty()
    public abstract Boolean colSubTotals();

    @Nullable
    @JsonProperty()
    public abstract Boolean showDimensionLabels();

    @Nullable
    @JsonProperty()
    public abstract Boolean percentStackedValues();

    @Nullable
    @JsonProperty()
    public abstract Boolean noSpaceBetweenColumns();

    @Nullable
    @JsonProperty()
    public abstract Boolean skipRounding();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbVisualizationLegendColumnAdapter.class)
    public abstract VisualizationLegend legend();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DisplayDensityColumnAdapter.class)
    public abstract DisplayDensity displayDensity();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DigitGroupSeparatorColumnAdapter.class)
    public abstract DigitGroupSeparator digitGroupSeparator();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(RelativePeriodsColumnAdapter.class)
    public abstract Map<RelativePeriod, Boolean> relativePeriods();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreCategoryDimensionListColumnAdapter.class)
    public abstract List<CategoryDimension> categoryDimensions();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(StringListColumnAdapter.class)
    public abstract List<String> filterDimensions();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(StringListColumnAdapter.class)
    public abstract List<String> rowDimensions();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(StringListColumnAdapter.class)
    public abstract List<String> columnDimensions();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreDataDimensionItemListColumnAdapter.class)
    public abstract List<DataDimensionItem> dataDimensionItems();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IntegerListColumnAdapter.class)
    public abstract List<Integer> organisationUnitLevels();

    @Nullable
    @JsonProperty()
    public abstract Boolean userOrganisationUnit();

    @Nullable
    @JsonProperty()
    public abstract Boolean userOrganisationUnitChildren();

    @Nullable
    @JsonProperty()
    public abstract Boolean userOrganisationUnitGrandChildren();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidListColumnAdapter.class)
    public abstract List<ObjectWithUid> organisationUnits();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidListColumnAdapter.class)
    public abstract List<ObjectWithUid> periods();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(AggregationTypeColumnAdapter.class)
    public abstract AggregationType aggregationType();

    public static Builder builder() {
        return new $$AutoValue_Visualization.Builder();
    }

    public static Visualization create(Cursor cursor) {
        return AutoValue_Visualization.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {

        public abstract Builder id(Long id);

        public abstract Builder description(String description);

        public abstract Builder displayDescription(String displayDescription);

        public abstract Builder displayFormName(String displayFormName);

        public abstract Builder title(String title);

        public abstract Builder displayTitle(String displayTitle);

        public abstract Builder subtitle(String subtitle);

        public abstract Builder displaySubtitle(String displaySubtitle);

        public abstract Builder type(VisualizationType type);

        public abstract Builder hideTitle(Boolean hideTitle);

        public abstract Builder hideSubtitle(Boolean hideSubtitle);

        public abstract Builder hideEmptyColumns(Boolean hideEmptyColumns);

        public abstract Builder hideEmptyRows(Boolean hideEmptyRows);

        public abstract Builder hideEmptyRowItems(HideEmptyItemStrategy hideEmptyRowItems);

        public abstract Builder hideLegend(Boolean hideLegend);

        public abstract Builder showHierarchy(Boolean showHierarchy);

        public abstract Builder rowTotals(Boolean rowTotals);

        public abstract Builder rowSubTotals(Boolean rowSubTotals);

        public abstract Builder colTotals(Boolean colTotals);

        public abstract Builder colSubTotals(Boolean colSubTotals);

        public abstract Builder showDimensionLabels(Boolean showDimensionLabels);

        public abstract Builder percentStackedValues(Boolean percentStackedValues);

        public abstract Builder noSpaceBetweenColumns(Boolean noSpaceBetweenColumns);

        public abstract Builder skipRounding(Boolean skipRounding);

        public abstract Builder displayDensity(DisplayDensity displayDensity);

        public abstract Builder legend(VisualizationLegend visualizationLegend);

        public abstract Builder digitGroupSeparator(DigitGroupSeparator digitGroupSeparator);

        public abstract Builder relativePeriods(Map<RelativePeriod, Boolean> relativePeriods);

        public abstract Builder categoryDimensions(List<CategoryDimension> categoryDimensions);

        public abstract Builder filterDimensions(List<String> filterDimensions);

        public abstract Builder rowDimensions(List<String> rowDimensions);

        public abstract Builder columnDimensions(List<String> columnDimensions);

        public abstract Builder dataDimensionItems(List<DataDimensionItem> dataDimensionItems);

        public abstract Builder organisationUnitLevels(List<Integer> organisationUnitLevels);

        public abstract Builder userOrganisationUnit(Boolean userOrganisationUnit);

        public abstract Builder userOrganisationUnitChildren(Boolean userOrganisationUnitChildren);

        public abstract Builder userOrganisationUnitGrandChildren(Boolean userOrganisationUnitGrandChildren);

        public abstract Builder organisationUnits(List<ObjectWithUid> organisationUnits);

        public abstract Builder periods(List<ObjectWithUid> periods);

        public abstract Builder aggregationType(AggregationType aggregationType);

        public abstract Visualization build();
    }
}