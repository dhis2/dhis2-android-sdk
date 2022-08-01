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

package org.hisp.dhis.android.core.imports.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import org.hisp.dhis.android.core.imports.ImportStatus;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class BaseImportSummary implements ImportSummary {

    private static final String IMPORT_COUNT = "importCount";
    private static final String IMPORT_STATUS = "status";
    private static final String RESPONSE_TYPE = "responseType";
    private static final String REFERENCE = "reference";
    private static final String IMPORT_CONFLICT = "conflicts";
    private static final String DESCRIPTION = "description";

    @NonNull
    @JsonProperty(IMPORT_COUNT)
    public abstract ImportCount importCount();

    @NonNull
    @JsonProperty(IMPORT_STATUS)
    public abstract ImportStatus status();

    @NonNull
    @JsonProperty(RESPONSE_TYPE)
    public abstract String responseType();

    //TODO: Reference SHOULD be annotated with NotNull. This is just a bug in ImportSummary response from server.
    @Nullable
    @JsonProperty(REFERENCE)
    public abstract String reference();

    @Nullable
    @JsonProperty(IMPORT_CONFLICT)
    public abstract List<ImportConflict> conflicts();

    @Nullable
    @JsonProperty(DESCRIPTION)
    public abstract String description();

    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder<T extends Builder> {

        public abstract T importCount(ImportCount importCount);

        public abstract T status(ImportStatus status);

        public abstract T responseType(String responseType);

        public abstract T reference(String reference);

        public abstract T conflicts(List<ImportConflict> conflicts);

        public abstract T description(String description);
    }
}
