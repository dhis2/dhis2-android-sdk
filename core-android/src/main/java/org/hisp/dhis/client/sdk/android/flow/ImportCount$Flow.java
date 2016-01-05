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
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.client.sdk.android.common.meta.DbDhis;
import org.hisp.dhis.client.sdk.models.common.importsummary.ImportCount;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class ImportCount$Flow extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    int imported;

    @Column
    int updated;

    @Column
    int ignored;

    @Column
    int deleted;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getImported() {
        return imported;
    }

    public void setImported(int imported) {
        this.imported = imported;
    }

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public int getIgnored() {
        return ignored;
    }

    public void setIgnored(int ignored) {
        this.ignored = ignored;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public ImportCount$Flow() {
        // empty constructor
    }

    public static ImportCount toModel(ImportCount$Flow importCountFlow) {
        if (importCountFlow == null) {
            return null;
        }

        ImportCount importCount = new ImportCount();
        importCount.setId(importCountFlow.getId());
        importCount.setImported(importCountFlow.getImported());
        importCount.setUpdated(importCountFlow.getUpdated());
        importCount.setIgnored(importCountFlow.getIgnored());
        importCount.setDeleted(importCountFlow.getDeleted());
        return importCount;
    }

    public static ImportCount$Flow fromModel(ImportCount importCount) {
        if (importCount == null) {
            return null;
        }

        ImportCount$Flow importCountFlow = new ImportCount$Flow();
        importCount.setId(importCountFlow.getId());
        importCount.setImported(importCountFlow.getImported());
        importCount.setUpdated(importCountFlow.getUpdated());
        importCount.setIgnored(importCountFlow.getIgnored());
        importCount.setDeleted(importCountFlow.getDeleted());
        return importCountFlow;
    }

    public static List<ImportCount> toModels(List<ImportCount$Flow> importCountFlows) {
        List<ImportCount> importCounts = new ArrayList<>();

        if (importCountFlows != null && !importCountFlows.isEmpty()) {
            for (ImportCount$Flow importCountFlow : importCountFlows) {
                importCounts.add(toModel(importCountFlow));
            }
        }

        return importCounts;
    }

    public static List<ImportCount$Flow> fromModels(List<ImportCount> importCounts) {
        List<ImportCount$Flow> importCountFlows = new ArrayList<>();

        if (importCounts != null && !importCounts.isEmpty()) {
            for (ImportCount importCount : importCounts) {
                importCountFlows.add(fromModel(importCount));
            }
        }

        return importCountFlows;
    }
}
