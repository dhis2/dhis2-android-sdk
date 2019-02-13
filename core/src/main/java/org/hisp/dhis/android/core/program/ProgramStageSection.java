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
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.database.IgnoreDataElementListColumnAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreProgramIndicatorListAdapter;
import org.hisp.dhis.android.core.data.database.ObjectWithUidColumnAdapter;
import org.hisp.dhis.android.core.data.database.ProgramStageSectionRenderingColumnAdapter;
import org.hisp.dhis.android.core.dataelement.DataElement;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramStageSection.Builder.class)
public abstract class ProgramStageSection extends BaseIdentifiableObject implements Model {

    @Nullable
    @JsonProperty()
    public abstract Integer sortOrder();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreProgramIndicatorListAdapter.class)
    public abstract List<ProgramIndicator> programIndicators();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreDataElementListColumnAdapter.class)
    public abstract List<DataElement> dataElements();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ProgramStageSectionRenderingColumnAdapter.class)
    public abstract ProgramStageSectionRendering renderType();

    @Nullable
    @JsonIgnore()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid programStage();

    @Nullable
    @JsonIgnore()
    @ColumnAdapter(ProgramStageSectionRenderingTypeColumnAdapter.class)
    public abstract ProgramStageSectionRenderingType desktopRenderType();

    @Nullable
    @JsonIgnore()
    @ColumnAdapter(ProgramStageSectionRenderingTypeColumnAdapter.class)
    public abstract ProgramStageSectionRenderingType mobileRenderType();

    public static Builder builder() {
        return new $$AutoValue_ProgramStageSection.Builder();
    }

    public static ProgramStageSection create(Cursor cursor) {
        return $AutoValue_ProgramStageSection.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder  extends BaseIdentifiableObject.Builder<Builder> {
        public abstract Builder id(Long id);

        public abstract Builder sortOrder(Integer sortOrder);

        public abstract Builder programIndicators(List<ProgramIndicator> programIndicators);

        public abstract Builder dataElements(List<DataElement> dataElements);

        public abstract Builder renderType(ProgramStageSectionRendering renderType);

        public abstract Builder programStage(ObjectWithUid programStage);

        public abstract Builder desktopRenderType(ProgramStageSectionRenderingType desktopRenderType);

        public abstract Builder mobileRenderType(ProgramStageSectionRenderingType mobileRenderType);

        public abstract ProgramStageSection build();
    }
}