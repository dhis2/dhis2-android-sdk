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

package org.hisp.dhis.android.core.enrollment.note;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;

@AutoValue
public abstract class Note229Compatible {
    private final static String VALUE = "value";
    private final static String STORED_BY = "storedBy";
    private final static String STORED_DATE = "storedDate";

    private static final Field<Note229Compatible, String> value = Field.create(VALUE);
    private static final Field<Note229Compatible, String> storedBy = Field.create(STORED_BY);
    private static final Field<Note229Compatible, String> storedDate= Field.create(STORED_DATE);

    public static final Fields<Note229Compatible> allFields = Fields.<Note229Compatible>builder().fields(
            value, storedBy, storedDate).build();

    @Nullable
    @JsonProperty(VALUE)
    public abstract String value();

    @Nullable
    @JsonProperty(STORED_BY)
    public abstract String storedBy();

    @Nullable
    @JsonProperty(STORED_DATE)
    public abstract String storedDate();

    @JsonCreator
    public static Note229Compatible create(
            @JsonProperty(VALUE) String value,
            @JsonProperty(STORED_BY) String storedBy,
            @JsonProperty(STORED_DATE) String storedDate) {

        return new AutoValue_Note229Compatible(value, storedBy, storedDate);
    }
}