/*
 * Copyright (c) 2004-2018, University of Oslo
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

package org.hisp.dhis.android.core.dataset;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.NestedField;

@AutoValue
public abstract class DataSetElement {
    private final static String DATA_ELEMENT = "dataElement";
    private final static String CATEGORY_COMBO = "categoryCombo";

    public static final NestedField<DataSetElement, ObjectWithUid> dataElement =
            NestedField.create(DATA_ELEMENT);

    public static final NestedField<DataSetElement, ObjectWithUid> categoryCombo =
            NestedField.create(CATEGORY_COMBO);

    public static final Fields<DataSetElement> allFields =
            Fields.<DataSetElement>builder().fields(dataElement.with(ObjectWithUid.uid),
                    categoryCombo.with(ObjectWithUid.uid)).build();

    @NonNull
    @JsonProperty(DATA_ELEMENT)
    public abstract ObjectWithUid dataElement();

    @Nullable
    @JsonProperty(CATEGORY_COMBO)
    public abstract ObjectWithUid categoryCombo();

    String categoryComboUid() {
        ObjectWithUid categoryCombo = categoryCombo();
        return categoryCombo == null ? null : categoryCombo.uid();
    }

    @JsonCreator
    public static DataSetElement create(
            @JsonProperty(DATA_ELEMENT) ObjectWithUid dataElement,
            @JsonProperty(CATEGORY_COMBO) ObjectWithUid categoryCombo) {

        return new AutoValue_DataSetElement(dataElement, categoryCombo);
    }
}