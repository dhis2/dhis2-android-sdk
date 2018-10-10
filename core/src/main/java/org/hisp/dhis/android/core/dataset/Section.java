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

package org.hisp.dhis.android.core.dataset;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.dataelement.DataElementOperandFields;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class Section extends BaseIdentifiableObject {

    private static final String DESCRIPTION = "description";
    private static final String SORT_ORDER = "sortOrder";
    private static final String DATA_SET = "dataSet";
    private static final String SHOW_ROW_TOTALS = "showRowTotals";
    private static final String SHOW_COLUMN_TOTALS = "showColumnTotals";
    private final static String DATA_ELEMENTS = "dataElements";
    private final static String GREYED_FIELDS = "greyedFields";


    private static final Field<Section, String> uid = Field.create(UID);
    private static final Field<Section, String> code = Field.create(CODE);
    private static final Field<Section, String> name = Field.create(NAME);
    private static final Field<Section, String> displayName = Field.create(DISPLAY_NAME);
    private static final Field<Section, String> created = Field.create(CREATED);
    private static final Field<Section, String> lastUpdated = Field.create(LAST_UPDATED);
    private static final Field<Section, Boolean> deleted = Field.create(DELETED);

    private static final Field<Section, String> description = Field.create(DESCRIPTION);
    private static final Field<Section, Integer> sortOrder = Field.create(SORT_ORDER);
    private static final NestedField<Section, ObjectWithUid> dataSet = NestedField.create(DATA_SET);
    private static final Field<Section, Boolean> showRowTotals = Field.create(SHOW_ROW_TOTALS);
    private static final Field<Section, Boolean> showColumnTotals = Field.create(SHOW_COLUMN_TOTALS);
    private static final NestedField<Section, ObjectWithUid> dataElements = NestedField.create(DATA_ELEMENTS);
    private static final NestedField<Section, DataElementOperand> greyedFields = NestedField.create(GREYED_FIELDS);


    static final Fields<Section> allFields = Fields.<Section>builder().fields(
            uid, code, name, displayName, created, lastUpdated, deleted,
            description, sortOrder, dataSet.with(ObjectWithUid.uid),
            showRowTotals, showColumnTotals,
            dataElements.with(ObjectWithUid.uid),
            greyedFields.with(DataElementOperandFields.allFields)
            ).build();

    @Nullable
    @JsonProperty(DESCRIPTION)
    public abstract String description();

    @Nullable
    @JsonProperty(SORT_ORDER)
    public abstract Integer sortOrder();

    @Nullable
    @JsonProperty(DATA_SET)
    public abstract ObjectWithUid dataSet();

    String dataSetUid() {
        ObjectWithUid dataSet = dataSet();
        return dataSet == null ? null : dataSet.uid();
    }

    @Nullable
    @JsonProperty(SHOW_ROW_TOTALS)
    public abstract Boolean showRowTotals();

    @Nullable
    @JsonProperty(SHOW_COLUMN_TOTALS)
    public abstract Boolean showColumnTotals();

    @Nullable
    @JsonProperty(DATA_ELEMENTS)
    public abstract List<ObjectWithUid> dataElements();

    @Nullable
    @JsonProperty(GREYED_FIELDS)
    public abstract List<DataElementOperand> greyedFields();


    @JsonCreator
    public static Section create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CODE) String code,
            @JsonProperty(NAME) String name,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,

            @JsonProperty(DESCRIPTION) String description,
            @JsonProperty(SORT_ORDER) Integer sortOrder ,
            @JsonProperty(DATA_SET) ObjectWithUid dataSet,
            @JsonProperty(SHOW_ROW_TOTALS) Boolean showRowTotals,
            @JsonProperty(SHOW_COLUMN_TOTALS) Boolean showColumnTotals,
            @JsonProperty(DATA_ELEMENTS) List<ObjectWithUid> dataElements,
            @JsonProperty(GREYED_FIELDS) List<DataElementOperand> greyedFields,
            @JsonProperty(DELETED) Boolean deleted) {

        return new AutoValue_Section(
                uid, code, name, displayName, created, lastUpdated, deleted, description, sortOrder,
                dataSet, showRowTotals, showColumnTotals, dataElements, greyedFields
        );
    }
}