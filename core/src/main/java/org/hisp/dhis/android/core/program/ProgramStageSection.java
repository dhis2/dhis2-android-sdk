/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.dataelement.DataElement;

import java.util.Date;
import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.safeUnmodifiableList;

@AutoValue
public abstract class ProgramStageSection extends BaseIdentifiableObject {
    private static final String SORT_ORDER = "sortOrder";
    private static final String PROGRAM_INDICATORS = "programIndicators";
    private static final String DATA_ELEMENTS = "dataElements";

    public static final Field<ProgramStageSection, String> uid = Field.create(UID);
    public static final Field<ProgramStageSection, String> code = Field.create(CODE);
    public static final Field<ProgramStageSection, String> name = Field.create(NAME);
    public static final Field<ProgramStageSection, String> displayName = Field.create(DISPLAY_NAME);
    public static final Field<ProgramStageSection, String> created = Field.create(CREATED);
    public static final Field<ProgramStageSection, String> lastUpdated = Field.create(LAST_UPDATED);
    public static final Field<ProgramStageSection, Integer> sortOrder = Field.create(SORT_ORDER);
    public static final Field<ProgramStageSection, Boolean> deleted = Field.create(DELETED);

    public static final NestedField<ProgramStageSection, ProgramIndicator> programIndicators =
            NestedField.create(PROGRAM_INDICATORS);

    public static final NestedField<ProgramStageSection, DataElement> dataElements = NestedField.create(DATA_ELEMENTS);

    @Nullable
    @JsonProperty(SORT_ORDER)
    public abstract Integer sortOrder();

    @Nullable
    @JsonProperty(PROGRAM_INDICATORS)
    public abstract List<ProgramIndicator> programIndicators();

    @Nullable
    @JsonProperty(DATA_ELEMENTS)
    public abstract List<DataElement> dataElements();

    @JsonCreator
    public static ProgramStageSection create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CODE) String code,
            @JsonProperty(NAME) String name,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(SORT_ORDER) Integer sortOrder,
            @JsonProperty(PROGRAM_INDICATORS) List<ProgramIndicator> programIndicators,
            @JsonProperty(DATA_ELEMENTS) List<DataElement> dataElements,
            @JsonProperty(DELETED) Boolean deleted) {

        return new AutoValue_ProgramStageSection(
                uid, code, name, displayName, created, lastUpdated, deleted, sortOrder,
                safeUnmodifiableList(programIndicators),
                safeUnmodifiableList(dataElements)
        );
    }
}