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

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class SearchGrid {
    private final static String HEADERS = "headers";
    private final static String META_DATA = "metaData";
    private final static String WIDTH = "width";
    private final static String HEIGHT = "height";
    private final static String ROWS = "rows";

    @NonNull
    @JsonProperty(HEADERS)
    abstract List<SearchGridHeader> headers();

    @NonNull
    @JsonProperty(META_DATA)
    abstract SearchGridMetadata metaData();

    @NonNull
    @JsonProperty(WIDTH)
    abstract Integer width();

    @NonNull
    @JsonProperty(HEIGHT)
    abstract Integer height();

    @NonNull
    @JsonProperty(ROWS)
    abstract List<List<String>> rows();

    @JsonCreator
    static SearchGrid create(
            @JsonProperty(HEADERS) List<SearchGridHeader> headers,
            @JsonProperty(META_DATA) SearchGridMetadata metaData,
            @JsonProperty(WIDTH) Integer width,
            @JsonProperty(HEIGHT) Integer height,
            @JsonProperty(ROWS) List<List<String>> rows) {

        return new AutoValue_SearchGrid(headers, metaData, width, height, rows);
    }
}
