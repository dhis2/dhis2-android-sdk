/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.common.meta.DbDhis;
import org.hisp.dhis.java.sdk.models.common.importsummary.Conflict;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class Conflict$Flow extends BaseModel$Flow {

    static final String IMPORT_SUMMARY_KEY = "importSummary";

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = IMPORT_SUMMARY_KEY, columnType = long.class, foreignColumnName = "id"),
            }, saveForeignKeyModel = false
    )
    ImportSummary$Flow importSummary;

    @Column
    String object;

    @Column
    String value;

    public ImportSummary$Flow getImportSummary() {
        return importSummary;
    }

    public void setImportSummary(ImportSummary$Flow importSummary) {
        this.importSummary = importSummary;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Conflict$Flow() {
        // empty constructor
    }

    public static Conflict toModel(Conflict$Flow conflictFlow) {
        if (conflictFlow == null) {
            return null;
        }

        Conflict conflict = new Conflict();
        conflict.setId(conflictFlow.getId());
        conflict.setImportSummary(ImportSummary$Flow.toModel(conflictFlow.getImportSummary()));
        conflict.setObject(conflictFlow.getObject());
        conflict.setValue(conflictFlow.getValue());

        return conflict;
    }

    public static Conflict$Flow fromModel(Conflict conflict) {
        if (conflict == null) {
            return null;
        }

        Conflict$Flow conflictFlow = new Conflict$Flow();
        conflict.setId(conflictFlow.getId());
        conflict.setImportSummary(ImportSummary$Flow.toModel(conflictFlow.getImportSummary()));
        conflict.setObject(conflictFlow.getObject());
        conflict.setValue(conflictFlow.getValue());
        return conflictFlow;
    }

    public static List<Conflict> toModels(List<Conflict$Flow> conflictFlows) {
        List<Conflict> conflicts = new ArrayList<>();

        if (conflictFlows != null && !conflictFlows.isEmpty()) {
            for (Conflict$Flow conflictFlow : conflictFlows) {
                conflicts.add(toModel(conflictFlow));
            }
        }

        return conflicts;
    }

    public static List<Conflict$Flow> fromModels(List<Conflict> conflicts) {
        List<Conflict$Flow> conflictFlows = new ArrayList<>();

        if (conflicts != null && !conflicts.isEmpty()) {
            for (Conflict conflict : conflicts) {
                conflictFlows.add(fromModel(conflict));
            }
        }

        return conflictFlows;
    }
}
