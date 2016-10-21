/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.models.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

public abstract class BaseNameableObject extends BaseIdentifiableObject implements NameableObject {
    private static final String JSON_PROPERTY_SHORT_NAME = "shortName";
    private static final String JSON_PROPERTY_DISPLAY_SHORT_NAME = "displayShortName";
    private static final String JSON_PROPERTY_DESCRIPTION = "description";
    private static final String JSON_PROPERTY_DISPLAY_DESCRIPTION = "displayDescription";

    @Override
    @Nullable
    @JsonProperty(JSON_PROPERTY_SHORT_NAME)
    public abstract String shortName();

    @Override
    @Nullable
    @JsonProperty(JSON_PROPERTY_DISPLAY_SHORT_NAME)
    public abstract String displayShortName();

    @Override
    @Nullable
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public abstract String description();

    @Override
    @Nullable
    @JsonProperty(JSON_PROPERTY_DISPLAY_DESCRIPTION)
    public abstract String displayDescription();

    protected static abstract class Builder<T extends Builder, E extends IdentifiableObject>
            extends BaseIdentifiableObject.Builder<T, E> {

        @JsonProperty(JSON_PROPERTY_SHORT_NAME)
        public abstract T shortName(@Nullable String shortName);

        @JsonProperty(JSON_PROPERTY_DISPLAY_SHORT_NAME)
        public abstract T displayShortName(@Nullable String displayShortName);

        @JsonProperty(JSON_PROPERTY_DESCRIPTION)
        public abstract T description(@Nullable String description);

        @JsonProperty(JSON_PROPERTY_DISPLAY_DESCRIPTION)
        public abstract T displayDescription(@Nullable String displayDescription);
    }
}
