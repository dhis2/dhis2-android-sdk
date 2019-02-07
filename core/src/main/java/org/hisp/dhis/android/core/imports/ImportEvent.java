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

package org.hisp.dhis.android.core.imports;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.safeUnmodifiableList;

@AutoValue
public abstract class ImportEvent {
    private static final String IMPORT_STATUS = "status";
    private static final String RESPONSE_TYPE = "responseType";
    private static final String IMPORTED = "imported";
    private static final String UPDATED = "updated";
    private static final String DELETED = "deleted";
    private static final String IGNORED = "ignored";
    private static final String IMPORT_SUMMARIES = "importSummaries";

    @NonNull
    @JsonProperty(IMPORT_STATUS)
    public abstract ImportStatus importStatus();

    @NonNull
    @JsonProperty(RESPONSE_TYPE)
    public abstract String responseType();

    @NonNull
    @JsonProperty(IMPORTED)
    public abstract Integer imported();

    @NonNull
    @JsonProperty(UPDATED)
    public abstract Integer updated();

    @NonNull
    @JsonProperty(DELETED)
    public abstract Integer deleted();

    @NonNull
    @JsonProperty(IGNORED)
    public abstract Integer ignored();

    @Nullable
    @JsonProperty(IMPORT_SUMMARIES)
    public abstract List<ImportSummary> importSummaries();

    @JsonCreator
    public static ImportEvent create(
            @JsonProperty(IMPORT_STATUS) ImportStatus importStatus,
            @JsonProperty(RESPONSE_TYPE) String responseType,
            @JsonProperty(IMPORTED) Integer imported,
            @JsonProperty(UPDATED) Integer updated,
            @JsonProperty(DELETED) Integer deleted,
            @JsonProperty(IGNORED) Integer ignored,
            @JsonProperty(IMPORT_SUMMARIES) List<ImportSummary> importSummaries) {
        return new AutoValue_ImportEvent(importStatus, responseType, imported, updated,
                deleted, ignored, safeUnmodifiableList(importSummaries));
    }
}
