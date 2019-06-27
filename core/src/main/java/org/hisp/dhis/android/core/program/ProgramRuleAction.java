/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.program;

import android.database.Cursor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.database.DataElementWithUidColumnAdapter;
import org.hisp.dhis.android.core.data.database.ObjectWithUidColumnAdapter;
import org.hisp.dhis.android.core.data.database.ProgramIndicatorWithUidColumnAdapter;
import org.hisp.dhis.android.core.data.database.ProgramStageSectionWithUidColumnAdapter;
import org.hisp.dhis.android.core.data.database.ProgramStageWithUidColumnAdapter;
import org.hisp.dhis.android.core.data.database.TrackedEntityAttributeWithUidColumnAdapter;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;

import androidx.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramRuleAction.Builder.class)
public abstract class ProgramRuleAction extends BaseIdentifiableObject implements Model {

    @Nullable
    @JsonProperty()
    public abstract String data();

    @Nullable
    @JsonProperty()
    public abstract String content();

    @Nullable
    @JsonProperty()
    public abstract String location();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(TrackedEntityAttributeWithUidColumnAdapter.class)
    public abstract TrackedEntityAttribute trackedEntityAttribute();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ProgramIndicatorWithUidColumnAdapter.class)
    public abstract ProgramIndicator programIndicator();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ProgramStageSectionWithUidColumnAdapter.class)
    public abstract ProgramStageSection programStageSection();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ProgramRuleActionTypeColumnAdapter.class)
    public abstract ProgramRuleActionType programRuleActionType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ProgramStageWithUidColumnAdapter.class)
    public abstract ProgramStage programStage();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DataElementWithUidColumnAdapter.class)
    public abstract DataElement dataElement();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid programRule();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid option();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid optionGroup();

    public static ProgramRuleAction create(Cursor cursor) {
        return $AutoValue_ProgramRuleAction.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ProgramRuleAction.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseIdentifiableObject.Builder<Builder> {
        public abstract Builder id(Long id);

        public abstract Builder data(String data);

        public abstract Builder content(String content);

        public abstract Builder location(String location);

        public abstract Builder trackedEntityAttribute(TrackedEntityAttribute trackedEntityAttribute);

        public abstract Builder programIndicator(ProgramIndicator programIndicator);

        public abstract Builder programStageSection(ProgramStageSection programStageSection);

        public abstract Builder programRuleActionType(ProgramRuleActionType programRuleActionType);

        public abstract Builder programStage(ProgramStage programStage);

        public abstract Builder dataElement(DataElement dataElement);

        public abstract Builder programRule(ObjectWithUid programRule);

        public abstract Builder option(ObjectWithUid option);

        public abstract Builder optionGroup(ObjectWithUid optionGroup);

        public abstract ProgramRuleAction build();
    }
}