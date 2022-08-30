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

package org.hisp.dhis.android.core.trackedentity.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import androidx.annotation.NonNull;

@AutoValue
abstract class SearchGridHeader {
    private final static String NAME = "name";
    private final static String COLUMN = "column";
    private final static String TYPE = "type";
    private final static String HIDDEN = "hidden";
    private final static String META = "meta";

    @NonNull
    @JsonProperty(NAME)
    abstract String name();

    @NonNull
    @JsonProperty(COLUMN)
    abstract String column();

    @NonNull
    @JsonProperty(TYPE)
    abstract String type();

    @NonNull
    @JsonProperty(HIDDEN)
    abstract Boolean hidden();

    @NonNull
    @JsonProperty(META)
    abstract Boolean meta();

    @JsonCreator
    static SearchGridHeader create(
            @JsonProperty(NAME) String name,
            @JsonProperty(COLUMN) String column,
            @JsonProperty(TYPE) String type,
            @JsonProperty(HIDDEN) Boolean hidden,
            @JsonProperty(META) Boolean meta) {

        return new AutoValue_SearchGridHeader(name, column, type, hidden, meta);
    }
}
