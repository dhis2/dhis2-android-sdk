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

package org.hisp.dhis.android.core.dataset;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.AccessColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.PeriodTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.ObjectWithUidColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreDataElementOperandListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreDataInputPeriodListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreDataSetElementListAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreIndicatorListAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreSectionListAdapter;
import org.hisp.dhis.android.core.arch.helpers.AccessHelper;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.indicator.Indicator;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_DataSet.Builder.class)
@SuppressWarnings({"PMD.GodClass", "PMD.ExcessivePublicCount"})
public abstract class DataSet extends BaseNameableObject
        implements CoreObject, ObjectWithStyle<DataSet, DataSet.Builder> {

    @Nullable
    @JsonProperty()
    @ColumnAdapter(PeriodTypeColumnAdapter.class)
    public abstract PeriodType periodType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid categoryCombo();

    @Nullable
    @JsonProperty()
    public abstract Boolean mobile();

    @Nullable
    @JsonProperty()
    public abstract Integer version();

    @Nullable
    @JsonProperty()
    public abstract Integer expiryDays();

    @Nullable
    @JsonProperty()
    public abstract Integer timelyDays();

    @Nullable
    @JsonProperty()
    public abstract Boolean notifyCompletingUser();

    @Nullable
    @JsonProperty()
    public abstract Integer openFuturePeriods();

    @Nullable
    @JsonProperty()
    public abstract Boolean fieldCombinationRequired();

    @Nullable
    @JsonProperty()
    public abstract Boolean validCompleteOnly();

    @Nullable
    @JsonProperty()
    public abstract Boolean noValueRequiresComment();

    @Nullable
    @JsonProperty()
    public abstract Boolean skipOffline();

    @Nullable
    @JsonProperty()
    public abstract Boolean dataElementDecoration();

    @Nullable
    @JsonProperty()
    public abstract Boolean renderAsTabs();

    @Nullable
    @JsonProperty()
    public abstract Boolean renderHorizontally();

    @Nullable
    @JsonProperty
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid workflow();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreDataSetElementListAdapter.class)
    public abstract List<DataSetElement> dataSetElements();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreIndicatorListAdapter.class)
    public abstract List<Indicator> indicators();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreSectionListAdapter.class)
    abstract List<Section> sections();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreDataElementOperandListColumnAdapter.class)
    public abstract List<DataElementOperand> compulsoryDataElementOperands();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreDataInputPeriodListColumnAdapter.class)
    public abstract List<DataInputPeriod> dataInputPeriods();

    @JsonProperty()
    @ColumnAdapter(AccessColumnAdapter.class)
    public abstract Access access();

    public static Builder builder() {
        return new $$AutoValue_DataSet.Builder();
    }

    public static DataSet create(Cursor cursor) {
        return $AutoValue_DataSet.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseNameableObject.Builder<Builder>
            implements ObjectWithStyle.Builder<DataSet, DataSet.Builder> {
        public abstract Builder id(Long id);

        public abstract Builder periodType(PeriodType periodType);

        public abstract Builder categoryCombo(ObjectWithUid categoryCombo);

        public abstract Builder mobile(Boolean mobile);

        public abstract Builder version(Integer version);

        public abstract Builder expiryDays(Integer expiryDays);

        public abstract Builder timelyDays(Integer timelyDays);

        public abstract Builder notifyCompletingUser(Boolean notifyCompletingUser);

        public abstract Builder openFuturePeriods(Integer openFuturePeriods);

        public abstract Builder fieldCombinationRequired(Boolean fieldCombinationRequired);

        public abstract Builder validCompleteOnly(Boolean validCompleteOnly);

        public abstract Builder noValueRequiresComment(Boolean noValueRequiresComment);

        public abstract Builder skipOffline(Boolean skipOffline);

        public abstract Builder dataElementDecoration(Boolean dataElementDecoration);

        public abstract Builder renderAsTabs(Boolean renderAsTabs);

        public abstract Builder renderHorizontally(Boolean renderHorizontally);

        public abstract Builder workflow(ObjectWithUid workflow);

        public abstract Builder dataSetElements(List<DataSetElement> dataSetElements);

        public abstract Builder indicators(List<Indicator> indicators);

        abstract Builder sections(List<Section> sections);

        public abstract Builder compulsoryDataElementOperands(List<DataElementOperand> compulsoryDataElementOperands);

        public abstract Builder dataInputPeriods(List<DataInputPeriod> dataInputPeriods);

        public abstract Builder access(Access access);

        abstract DataSet autoBuild();

        // Auxiliary fields
        abstract Access access();
        abstract ObjectStyle style();

        public DataSet build() {
            try {
                access();
            } catch (IllegalStateException e) {
                access(AccessHelper.defaultAccess());
            }
            
            try {
                style();
            } catch (IllegalStateException e) {
                style(ObjectStyle.builder().build());
            }

            return autoBuild();
        }
    }
}