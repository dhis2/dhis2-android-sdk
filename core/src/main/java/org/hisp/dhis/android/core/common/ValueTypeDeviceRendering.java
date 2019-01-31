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

package org.hisp.dhis.android.core.common;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ValueTypeDeviceRendering {
    private static final String TYPE = "type";
    private static final String MIN = "min";
    private static final String MAX = "max";
    private static final String STEP = "step";
    private static final String DECIMAL_POINTS = "decimalPoints";

    @Nullable
    @JsonProperty(TYPE)
    public abstract ValueTypeRenderingType type();

    @Nullable
    @JsonProperty(MIN)
    public abstract Integer min();

    @Nullable
    @JsonProperty(MAX)
    public abstract Integer max();

    @Nullable
    @JsonProperty(STEP)
    public abstract Integer step();

    @Nullable
    @JsonProperty(DECIMAL_POINTS)
    public abstract Integer decimalPoints();

    @JsonCreator
    public static ValueTypeDeviceRendering create(
            @JsonProperty(TYPE) ValueTypeRenderingType type,
            @JsonProperty(MIN) Integer min,
            @JsonProperty(MAX) Integer max,
            @JsonProperty(STEP) Integer step,
            @JsonProperty(DECIMAL_POINTS) Integer decimalPoints) {

        return new AutoValue_ValueTypeDeviceRendering(type, min, max, step, decimalPoints);
    }
}