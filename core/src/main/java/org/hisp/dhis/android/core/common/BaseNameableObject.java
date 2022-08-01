/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.common;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import androidx.annotation.Nullable;

public abstract class BaseNameableObject extends BaseIdentifiableObject implements NameableObject {
    public static final String SHORT_NAME = "shortName";
    public static final String DISPLAY_SHORT_NAME = "displayShortName";
    public static final String DESCRIPTION = "description";
    public static final String DISPLAY_DESCRIPTION = "displayDescription";

    @Nullable
    @Override
    public abstract String shortName();

    @Nullable
    @Override
    public abstract String displayShortName();

    @Nullable
    @Override
    public abstract String description();

    @Nullable
    @Override
    public abstract String displayDescription();

    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder<T extends Builder> extends BaseIdentifiableObject.Builder<T> {

        public abstract T shortName(@Nullable String shortName);

        public abstract T displayShortName(@Nullable String displayShortName);

        public abstract T description(@Nullable String description);

        public abstract T displayDescription(@Nullable String displayDescription);
    }
}
