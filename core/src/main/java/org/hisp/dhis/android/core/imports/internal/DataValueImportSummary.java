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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.imports.ImportStatus;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.safeUnmodifiableList;

@AutoValue
public abstract class DataValueImportSummary {

    private static final String IMPORT_COUNT = "importCount";
    private static final String IMPORT_STATUS = "status";
    private static final String RESPONSE_TYPE = "responseType";
    private static final String REFERENCE = "reference";
    private static final String IMPORT_CONFLICT = "conflicts";

    public static final DataValueImportSummary EMPTY = DataValueImportSummary.create(
            ImportCount.EMPTY,
            ImportStatus.SUCCESS,
            "ImportSummary",
            null, null
    );

    @NonNull
    @JsonProperty(IMPORT_COUNT)
    public abstract ImportCount importCount();

    @NonNull
    @JsonProperty(IMPORT_STATUS)
    public abstract ImportStatus importStatus();

    @NonNull
    @JsonProperty(RESPONSE_TYPE)
    public abstract String responseType();

    //TODO: Reference SHOULD be annotated with NotNull. This is just a bug in ImportSummary response from server.
    @Nullable
    @JsonProperty(REFERENCE)
    public abstract String reference();

    @Nullable
    @JsonProperty(IMPORT_CONFLICT)
    public abstract List<ImportConflict> importConflicts();

    @JsonCreator
    public static DataValueImportSummary create(
            @JsonProperty(IMPORT_COUNT) ImportCount importCount,
            @JsonProperty(IMPORT_STATUS) ImportStatus importStatus,
            @JsonProperty(RESPONSE_TYPE) String responseType,
            @JsonProperty(REFERENCE) String reference,
            @JsonProperty(IMPORT_CONFLICT) List<ImportConflict> importConflicts) {
        return new AutoValue_DataValueImportSummary(importCount, importStatus,
                responseType, reference,
                safeUnmodifiableList(importConflicts));
    }
}
