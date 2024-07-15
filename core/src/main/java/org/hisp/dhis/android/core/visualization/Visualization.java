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

package org.hisp.dhis.android.core.visualization;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbVisualizationLegendColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.AggregationTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.DigitGroupSeparatorColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.DisplayDensityColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.HideEmptyItemStrategyColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.VisualizationTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreVisualizationDimensionListColumnAdapter;
import org.hisp.dhis.android.core.common.AggregationType;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.CoreObject;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_Visualization.Builder.class)
@SuppressWarnings({"PMD.ExcessivePublicCount"})
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
    @ColumnAdapter(AggregationTypeColumnAdapter.class)
    public abstract AggregationType aggregationType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreVisualizationDimensionListColumnAdapter.class)
    public abstract List<VisualizationDimension> columns();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreVisualizationDimensionListColumnAdapter.class)
    public abstract List<VisualizationDimension> rows();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreVisualizationDimensionListColumnAdapter.class)
    public abstract List<VisualizationDimension> filters();

    public static Builder builder() {
        return new $$AutoValue_Visualization.Builder();
    }

    public static Visualization create(Cursor cursor) {
        return AutoValue_Visualization.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseIdentifiableObject.Builder<Builder> {

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

        public abstract Builder aggregationType(AggregationType aggregationType);

        public abstract Builder columns(List<VisualizationDimension> columns);

        public abstract Builder rows(List<VisualizationDimension> rows);

        public abstract Builder filters(List<VisualizationDimension> filters);

        public abstract Visualization build();
    }
}