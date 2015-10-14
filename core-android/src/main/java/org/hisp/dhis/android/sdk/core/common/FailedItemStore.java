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

package org.hisp.dhis.android.sdk.core.common;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.flow.Conflict$Flow;
import org.hisp.dhis.android.sdk.core.flow.Conflict$Flow$Table;
import org.hisp.dhis.android.sdk.core.flow.FailedItem$Flow;
import org.hisp.dhis.android.sdk.core.flow.FailedItem$Flow$Table;
import org.hisp.dhis.android.sdk.core.flow.ImportSummary$Flow;
import org.hisp.dhis.android.sdk.models.common.faileditem.FailedItem;
import org.hisp.dhis.android.sdk.models.common.faileditem.FailedItemType;
import org.hisp.dhis.android.sdk.models.common.faileditem.IFailedItemStore;

import java.util.List;

public final class FailedItemStore implements IFailedItemStore {

    public FailedItemStore() {
        //empty constructor
    }

    @Override
    public boolean insert(FailedItem object) {
        FailedItem$Flow failedItemFlow = FailedItem$Flow.fromModel(object);
        ImportSummary$Flow importSummaryFlow = failedItemFlow.getImportSummary();
        if(importSummaryFlow != null) {
            List<Conflict$Flow> conflicts = new Select()
                    .from(Conflict$Flow.class)
                    .where(Condition.column(Conflict$Flow$Table
                            .IMPORTSUMMARY_IMPORTSUMMARY)
                            .is(importSummaryFlow)).queryList();
            for(Conflict$Flow conflictFlow : conflicts) {
                conflictFlow.insert();
            }
        }
        failedItemFlow.insert();
        return true;
    }

    @Override
    public boolean update(FailedItem object) {
        FailedItem$Flow failedItemFlow = FailedItem$Flow.fromModel(object);
        ImportSummary$Flow importSummaryFlow = failedItemFlow.getImportSummary();
        if(importSummaryFlow != null) {
            List<Conflict$Flow> conflicts = new Select()
                    .from(Conflict$Flow.class)
                    .where(Condition.column(Conflict$Flow$Table
                            .IMPORTSUMMARY_IMPORTSUMMARY)
                            .is(importSummaryFlow))
                    .queryList();
            for(Conflict$Flow conflictFlow : conflicts) {
                conflictFlow.update();
            }
        }
        failedItemFlow.update();
        return true;
    }

    @Override
    public boolean save(FailedItem object) {
        FailedItem$Flow failedItemFlow = FailedItem$Flow.fromModel(object);
        ImportSummary$Flow importSummaryFlow = failedItemFlow.getImportSummary();
        if(importSummaryFlow != null) {
            List<Conflict$Flow> conflicts = new Select()
                    .from(Conflict$Flow.class)
                    .where(Condition.column(Conflict$Flow$Table
                            .IMPORTSUMMARY_IMPORTSUMMARY)
                            .is(importSummaryFlow))
                    .queryList();
            for(Conflict$Flow conflictFlow : conflicts) {
                conflictFlow.save();
            }
        }
        failedItemFlow.save();
        return true;
    }

    @Override
    public boolean delete(FailedItem object) {
        FailedItem$Flow failedItemFlow = FailedItem$Flow.fromModel(object);
        ImportSummary$Flow importSummaryFlow = failedItemFlow.getImportSummary();
        if(importSummaryFlow != null) {
            List<Conflict$Flow> conflicts = new Select()
                    .from(Conflict$Flow.class)
                    .where(Condition.column(Conflict$Flow$Table
                            .IMPORTSUMMARY_IMPORTSUMMARY)
                            .is(importSummaryFlow))
                    .queryList();
            for(Conflict$Flow conflictFlow : conflicts) {
                conflictFlow.delete();
            }
        }
        failedItemFlow.delete();
        return true;
    }

    @Override
    public FailedItem queryById(long id) {
        return null;
    }

    @Override
    public List<FailedItem> queryAll() {
        List<FailedItem$Flow> failedItemFlows = new Select()
                .from(FailedItem$Flow.class)
                .queryList();
        for(FailedItem$Flow failedItemFlow : failedItemFlows) {
            ImportSummary$Flow importSummaryFlow = failedItemFlow.getImportSummary();
            if(importSummaryFlow != null) {
                List<Conflict$Flow> conflicts = new Select().from(Conflict$Flow.class).where(Condition.column(Conflict$Flow$Table.IMPORTSUMMARY_IMPORTSUMMARY).is(importSummaryFlow)).queryList();
                importSummaryFlow.setConflicts(conflicts);
            }
        }
        return FailedItem$Flow.toModels(failedItemFlows);
    }

    @Override
    public List<FailedItem> query(FailedItemType type) {
        List<FailedItem$Flow> failedItemFlows = new Select()
                .from(FailedItem$Flow.class).where(Condition.column(FailedItem$Flow$Table.ITEMTYPE).is(type))
                .queryList();
        for(FailedItem$Flow failedItemFlow : failedItemFlows) {
            ImportSummary$Flow importSummaryFlow = failedItemFlow.getImportSummary();
            if(importSummaryFlow != null) {
                List<Conflict$Flow> conflicts = new Select().from(Conflict$Flow.class).where(Condition.column(Conflict$Flow$Table.IMPORTSUMMARY_IMPORTSUMMARY).is(importSummaryFlow)).queryList();
                importSummaryFlow.setConflicts(conflicts);
            }
        }
        return FailedItem$Flow.toModels(failedItemFlows);
    }

    @Override
    public FailedItem query(FailedItemType type, long itemId) {
        FailedItem$Flow failedItemFlow = new Select()
                .from(FailedItem$Flow.class).where(Condition.column(FailedItem$Flow$Table.ITEMTYPE)
                        .is(type)).and(Condition.column(FailedItem$Flow$Table.ITEMID).is(itemId))
                .querySingle();
        ImportSummary$Flow importSummaryFlow = failedItemFlow.getImportSummary();
        if(importSummaryFlow != null) {
            List<Conflict$Flow> conflicts = new Select().from(Conflict$Flow.class).where(Condition.column(Conflict$Flow$Table.IMPORTSUMMARY_IMPORTSUMMARY).is(importSummaryFlow)).queryList();
            importSummaryFlow.setConflicts(conflicts);
        }
        return FailedItem$Flow.toModel(failedItemFlow);
    }
}
