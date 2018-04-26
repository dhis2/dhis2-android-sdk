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

package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.data.api.NestedField;

import java.util.List;

@AutoValue
public abstract class TrackedEntityAttributeRequiredAndOptionalValue {
    private final static String REQUIRED = "REQUIRED";
    private final static String OPTIONAL = "OPTIONAL";

    private static final NestedField<TrackedEntityAttributeRequiredAndOptionalValue, String> required =
            NestedField.create(REQUIRED);
    private static final NestedField<TrackedEntityAttributeRequiredAndOptionalValue, String> optional =
            NestedField.create(OPTIONAL);

    @Nullable
    @JsonProperty(REQUIRED)
    public abstract List<String> required();

    @Nullable
    @JsonProperty(OPTIONAL)
    public abstract List<String> optional();

    @JsonCreator
    public static TrackedEntityAttributeRequiredAndOptionalValue create(
            @JsonProperty(REQUIRED) List<String> required,
            @JsonProperty(OPTIONAL) List<String> optional) {

        return new AutoValue_TrackedEntityAttributeRequiredValue(required, optional);
    }
}
