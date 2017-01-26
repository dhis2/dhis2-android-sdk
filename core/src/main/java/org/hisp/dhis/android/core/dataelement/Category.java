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

package org.hisp.dhis.android.core.dataelement;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObject;

import java.util.Collections;
import java.util.List;

// TODO: Write Category- Store, StoreImp, Model and their tests (Datacapture)
@AutoValue
@JsonDeserialize(builder = AutoValue_Category.Builder.class)
public abstract class Category extends BaseNameableObject {
    private static final String JSON_PROPERTY_CATEGORY_OPTIONS = "categoryOptions";

    @Nullable
    @JsonProperty(JSON_PROPERTY_CATEGORY_OPTIONS)
    public abstract List<CategoryOption> categoryOptions();

    public static Builder builder() {
        return new AutoValue_Category.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObject.Builder<Builder> {

        @JsonProperty(JSON_PROPERTY_CATEGORY_OPTIONS)
        public abstract Builder categoryOptions(@Nullable List<CategoryOption> categoryOptions);

        // used only to support unmodifiable collections
        abstract List<CategoryOption> categoryOptions();

        // used only to support unmodifiable collections
        abstract Category autoBuild();

        public Category build() {
            if (categoryOptions() != null) {
                categoryOptions(Collections.unmodifiableList(categoryOptions()));
            }

            return autoBuild();
        }
    }
}
