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

package org.hisp.dhis.android.core.trackedentity;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbValueTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DefaultAccessColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.AggregationTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.ObjectWithUidColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreObjectWithUidListColumnAdapter;
import org.hisp.dhis.android.core.arch.helpers.AccessHelper;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.AggregationType;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeFields;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_TrackedEntityAttribute.Builder.class)
public abstract class TrackedEntityAttribute extends BaseNameableObject
        implements CoreObject, ObjectWithStyle<TrackedEntityAttribute, TrackedEntityAttribute.Builder>  {

    @Nullable
    @JsonProperty()
    public abstract String pattern();

    @Nullable
    @JsonProperty()
    public abstract Integer sortOrderInListNoProgram();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid optionSet();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbValueTypeColumnAdapter.class)
    public abstract ValueType valueType();

    @Nullable
    @JsonProperty()
    public abstract String expression();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(AggregationTypeColumnAdapter.class)
    public abstract AggregationType aggregationType();

    @Nullable
    @JsonProperty()
    public abstract Boolean programScope();

    @Nullable
    @JsonProperty()
    public abstract Boolean displayInListNoProgram();

    @Nullable
    @JsonProperty()
    public abstract Boolean generated();

    @Nullable
    @JsonProperty()
    public abstract Boolean displayOnVisitSchedule();

    @Nullable
    @JsonProperty(TrackedEntityAttributeFields.ORG_UNIT_SCOPE)
    @ColumnName(TrackedEntityAttributeFields.ORG_UNIT_SCOPE)
    public abstract Boolean orgUnitScope();

    @Nullable
    @JsonProperty()
    @ColumnName(TrackedEntityAttributeTableInfo.Columns.UNIQUE)
    public abstract Boolean unique();

    @Nullable
    @JsonProperty()
    public abstract Boolean inherit();

    @Nullable
    @JsonProperty()
    public abstract String fieldMask();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreObjectWithUidListColumnAdapter.class)
    public abstract List<ObjectWithUid> legendSets();

    @JsonProperty()
    @ColumnAdapter(DefaultAccessColumnAdapter.class)
    public abstract Access access();

    @Nullable
    @JsonProperty()
    public abstract String formName();

    @Nullable
    @JsonProperty()
    public abstract String displayFormName();

    public static Builder builder() {
        return new $$AutoValue_TrackedEntityAttribute.Builder();
    }

    public static TrackedEntityAttribute create(Cursor cursor) {
        return $AutoValue_TrackedEntityAttribute.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseNameableObject.Builder<Builder>
            implements ObjectWithStyle.Builder<TrackedEntityAttribute, TrackedEntityAttribute.Builder> {

        public abstract Builder id(Long id);

        public abstract Builder pattern(String pattern);

        public abstract Builder sortOrderInListNoProgram(Integer sortOrderInListNoProgram);

        public abstract Builder optionSet(ObjectWithUid optionSet);

        public abstract Builder valueType(ValueType valueType);

        public abstract Builder expression(String expression);

        public abstract Builder aggregationType(AggregationType aggregationType);

        public abstract Builder programScope(Boolean programScope);

        public abstract Builder displayInListNoProgram(Boolean displayInListNoProgram);

        public abstract Builder generated(Boolean generated);

        public abstract Builder displayOnVisitSchedule(Boolean displayOnVisitSchedule);

        @JsonProperty(TrackedEntityAttributeFields.ORG_UNIT_SCOPE)
        public abstract Builder orgUnitScope(Boolean orgUnitScope);

        public abstract Builder unique(Boolean unique);

        public abstract Builder inherit(Boolean inherit);

        public abstract Builder fieldMask(String fieldMask);

        public abstract Builder style(ObjectStyle style);

        public abstract Builder access(Access access);

        public abstract Builder legendSets(List<ObjectWithUid> legendSets);

        public abstract Builder formName(String formName);

        public abstract Builder displayFormName(String displayFormName);

        abstract TrackedEntityAttribute autoBuild();

        // Auxiliary fields
        abstract Access access();
        abstract ObjectStyle style();

        public TrackedEntityAttribute build() {
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