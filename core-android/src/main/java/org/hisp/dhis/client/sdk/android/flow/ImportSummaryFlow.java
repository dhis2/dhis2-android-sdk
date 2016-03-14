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

package org.hisp.dhis.client.sdk.android.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.client.sdk.android.common.meta.DbDhis;
import org.hisp.dhis.client.sdk.models.common.importsummary.ImportSummary;

import java.util.List;

@Table(database = DbDhis.class)
public final class ImportSummaryFlow extends BaseModelFlow {

    static final String IMPORTCOUNT_KEY = "importCount";

    @Column
    ImportSummary.Status status;

    @Column
    String description;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = IMPORTCOUNT_KEY,
                            columnType = long.class, foreignKeyColumnName = "id"),
            }, saveForeignKeyModel = true
    )
    ImportCountFlow importCount;

    @Column
    String reference;

    @Column
    String href;

    List<ConflictFlow> conflicts;

    public ImportSummary.Status getStatus() {
        return status;
    }

    public void setStatus(ImportSummary.Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ImportCountFlow getImportCount() {
        return importCount;
    }

    public void setImportCount(ImportCountFlow importCount) {
        this.importCount = importCount;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public List<ConflictFlow> getConflicts() {
        return conflicts;
    }

    public void setConflicts(List<ConflictFlow> conflicts) {
        this.conflicts = conflicts;
    }

    public ImportSummaryFlow() {
        // empty constructor
    }
}
