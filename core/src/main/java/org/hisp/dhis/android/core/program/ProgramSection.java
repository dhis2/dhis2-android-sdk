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
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectStyleFields;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.NestedField;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class ProgramSection extends BaseIdentifiableObject {
    private static final String DESCRIPTION = "description";
    private static final String PROGRAM = "program";
    private static final String ATTRIBUTES = "programTrackedEntityAttribute";
    private static final String SORT_ORDER = "sortOrder";
    private static final String STYLE = "style";
    private static final String FORM_NAME = "formName";

    private static final Field<ProgramSection, String> uid = Field.create(UID);
    private static final Field<ProgramSection, String> code = Field.create(CODE);
    private static final Field<ProgramSection, String> name = Field.create(NAME);
    private static final Field<ProgramSection, String> displayName = Field.create(DISPLAY_NAME);
    private static final Field<ProgramSection, String> created = Field.create(CREATED);
    private static final Field<ProgramSection, String> lastUpdated = Field.create(LAST_UPDATED);
    private static final Field<ProgramSection, Boolean> deleted = Field.create(DELETED);

    private static final Field<ProgramSection, String> description = Field.create(DESCRIPTION);
    private static final NestedField<ProgramSection, ObjectWithUid> program = NestedField.create(PROGRAM);
    private static final NestedField<ProgramSection, ObjectWithUid> attributes =
            NestedField.create(ATTRIBUTES);
    private static final Field<ProgramSection, Integer> sortOrder = Field.create(SORT_ORDER);
    private static final NestedField<ProgramSection, ObjectStyle> style = NestedField.create(STYLE);
    private static final Field<ProgramSection, String> formName = Field.create(FORM_NAME);

    static final Fields<ProgramSection> allFields = Fields.<ProgramSection>builder().fields(
            uid, code, name, displayName, created, lastUpdated, deleted, description, program.with(ObjectWithUid.uid),
            sortOrder, formName, attributes.with(ObjectWithUid.uid), style.with(ObjectStyleFields.allFields)).build();

    @Nullable
    @JsonProperty(DESCRIPTION)
    public abstract String description();

    @Nullable
    @JsonProperty(PROGRAM)
    public abstract ObjectWithUid program();

    String programUid() {
        ObjectWithUid program = program();
        return program == null ? null : program.uid();
    }

    @Nullable
    @JsonProperty(ATTRIBUTES)
    public abstract List<ObjectWithUid> attributes();

    @Nullable
    @JsonProperty(SORT_ORDER)
    public abstract Integer sortOrder();

    @Nullable
    @JsonProperty(STYLE)
    public abstract ObjectStyle style();

    @Nullable
    @JsonProperty(FORM_NAME)
    public abstract String formName();

    @JsonCreator
    public static ProgramSection create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CODE) String code,
            @JsonProperty(NAME) String name,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,

            @JsonProperty(DESCRIPTION) String description,
            @JsonProperty(PROGRAM) ObjectWithUid program,
            @JsonProperty(ATTRIBUTES) List<ObjectWithUid> attributes,
            @JsonProperty(SORT_ORDER) Integer sortOrder,
            @JsonProperty(STYLE) ObjectStyle style,
            @JsonProperty(FORM_NAME) String formName,
            @JsonProperty(DELETED) Boolean deleted) {

        return new AutoValue_ProgramSection(
                uid, code, name, displayName, created, lastUpdated, deleted, description, program,
                attributes, sortOrder, style, formName
        );
    }
}