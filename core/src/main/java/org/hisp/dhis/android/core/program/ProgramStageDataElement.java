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

@AutoValue
public abstract class ProgramStageDataElement extends BaseIdentifiableObject {
    private static final String DISPLAY_IN_REPORTS = "displayInReports";
    private static final String DATA_ELEMENT = "dataElement";
    private static final String COMPULSORY = "compulsory";
    private static final String ALLOW_PROVIDED_ELSEWHERE = "allowProvidedElsewhere";
    private static final String SORT_ORDER = "sortOrder";
    private static final String ALLOW_FUTURE_DATE = "allowFutureDate";
    private static final String PROGRAM_STAGE = "programStage";

    public static final Field<ProgramStageDataElement, String> uid
            = Field.create(UID);
    public static final Field<ProgramStageDataElement, String> code
            = Field.create(CODE);
    public static final Field<ProgramStageDataElement, String> name
            = Field.create(NAME);
    public static final Field<ProgramStageDataElement, String> displayName
            = Field.create(DISPLAY_NAME);
    public static final Field<ProgramStageDataElement, String> created
            = Field.create(CREATED);
    public static final Field<ProgramStageDataElement, String> lastUpdated
            = Field.create(LAST_UPDATED);
    public static final Field<ProgramStageDataElement, String> displayInReports
            = Field.create(DISPLAY_IN_REPORTS);
    public static final Field<ProgramStageDataElement, Boolean> compulsory
            = Field.create(COMPULSORY);
    public static final Field<ProgramStageDataElement, Boolean> allowProvidedElsewhere
            = Field.create(ALLOW_PROVIDED_ELSEWHERE);
    public static final Field<ProgramStageDataElement, Integer> sortOrder
            = Field.create(SORT_ORDER);
    public static final Field<ProgramStageDataElement, Boolean> allowFutureDate
            = Field.create(ALLOW_FUTURE_DATE);
    public static final NestedField<ProgramStageDataElement, DataElement> dataElement
            = NestedField.create(DATA_ELEMENT);
    public static final NestedField<ProgramStageDataElement, ProgramStage> programStage
            = NestedField.create(PROGRAM_STAGE);
    public static final Field<ProgramStageDataElement, Boolean> deleted
            = Field.create(DELETED);

    @Nullable
    @JsonProperty(DISPLAY_IN_REPORTS)
    public abstract Boolean displayInReports();

    @Nullable
    @JsonProperty(COMPULSORY)
    public abstract Boolean compulsory();

    @Nullable
    @JsonProperty(ALLOW_PROVIDED_ELSEWHERE)
    public abstract Boolean allowProvidedElsewhere();

    @Nullable
    @JsonProperty(SORT_ORDER)
    public abstract Integer sortOrder();

    @Nullable
    @JsonProperty(ALLOW_FUTURE_DATE)
    public abstract Boolean allowFutureDate();

    @Nullable
    @JsonProperty(DATA_ELEMENT)
    public abstract DataElement dataElement();

    @Nullable
    @JsonProperty(PROGRAM_STAGE)
    public abstract ProgramStage programStage();

    @JsonCreator
    public static ProgramStageDataElement create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CODE) String code,
            @JsonProperty(NAME) String name,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(DISPLAY_IN_REPORTS) Boolean displayInReports,
            @JsonProperty(COMPULSORY) Boolean compulsory,
            @JsonProperty(ALLOW_PROVIDED_ELSEWHERE) Boolean allowProvidedElsewhere,
            @JsonProperty(SORT_ORDER) Integer sortOrder,
            @JsonProperty(ALLOW_FUTURE_DATE) Boolean allowFutureDate,
            @JsonProperty(DATA_ELEMENT) DataElement dataElement,
            @JsonProperty(DELETED) Boolean deleted,
            @JsonProperty(PROGRAM_STAGE) ProgramStage programStage
    ) {

        return new AutoValue_ProgramStageDataElement(
                uid,
                code,
                name,
                displayName,
                created,
                lastUpdated,
                deleted,
                displayInReports,
                compulsory,
                allowProvidedElsewhere,
                sortOrder,
                allowFutureDate,
                dataElement,
                programStage
        );
    }
}
