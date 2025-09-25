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

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.AggregationType;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.CoreObject;

import java.util.List;

@AutoValue
@SuppressWarnings({"PMD.ExcessivePublicCount"})
public abstract class Visualization extends BaseIdentifiableObject implements CoreObject {

    @Nullable
    public abstract String description();

    @Nullable
    public abstract String displayDescription();

    @Nullable
    public abstract String displayFormName();

    @Nullable
    public abstract String title();

    @Nullable
    public abstract String displayTitle();

    @Nullable
    public abstract String subtitle();

    @Nullable
    public abstract String displaySubtitle();

    @Nullable
    public abstract VisualizationType type();

    @Nullable
    public abstract Boolean hideTitle();

    @Nullable
    public abstract Boolean hideSubtitle();

    @Nullable
    public abstract Boolean hideEmptyColumns();

    @Nullable
    public abstract Boolean hideEmptyRows();

    @Nullable
    public abstract HideEmptyItemStrategy hideEmptyRowItems();

    @Nullable
    public abstract Boolean hideLegend();

    @Nullable
    public abstract Boolean showHierarchy();

    @Nullable
    public abstract Boolean rowTotals();

    @Nullable
    public abstract Boolean rowSubTotals();

    @Nullable
    public abstract Boolean colTotals();

    @Nullable
    public abstract Boolean colSubTotals();

    @Nullable
    public abstract Boolean showDimensionLabels();

    @Nullable
    public abstract Boolean percentStackedValues();

    @Nullable
    public abstract Boolean noSpaceBetweenColumns();

    @Nullable
    public abstract Boolean skipRounding();

    @Nullable
    public abstract VisualizationLegend legend();

    @Nullable
    public abstract DisplayDensity displayDensity();

    @Nullable
    public abstract DigitGroupSeparator digitGroupSeparator();

    @Nullable
    public abstract AggregationType aggregationType();

    @Nullable
    public abstract List<VisualizationDimension> columns();

    @Nullable
    public abstract List<VisualizationDimension> rows();

    @Nullable
    public abstract List<VisualizationDimension> filters();

    public static Builder builder() {
        return new AutoValue_Visualization.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder extends BaseIdentifiableObject.Builder<Builder> {

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
