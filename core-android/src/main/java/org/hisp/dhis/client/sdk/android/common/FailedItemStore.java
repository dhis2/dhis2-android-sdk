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

package org.hisp.dhis.client.sdk.android.common;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.flow.ConflictFlow;
import org.hisp.dhis.client.sdk.android.flow.ConflictFlow_Table;
import org.hisp.dhis.client.sdk.android.flow.FailedItemFlow;
import org.hisp.dhis.client.sdk.android.flow.FailedItemFlow_Table;
import org.hisp.dhis.client.sdk.android.flow.ImportSummaryFlow;
import org.hisp.dhis.client.sdk.core.common.IFailedItemStore;
import org.hisp.dhis.client.sdk.models.common.faileditem.FailedItem;
import org.hisp.dhis.client.sdk.models.common.faileditem.FailedItemType;

import java.util.List;

public final class FailedItemStore implements IFailedItemStore {

    public FailedItemStore() {
        //empty constructor
    }

    @Override
    public boolean insert(FailedItem object) {
        FailedItemFlow failedItemFlow = null;
        ImportSummaryFlow importSummaryFlow = failedItemFlow.getImportSummary();

        if (importSummaryFlow != null) {
            List<ConflictFlow> conflicts = new Select()
                    .from(ConflictFlow.class)
                    .where(ConflictFlow_Table
                            .importSummary.is(importSummaryFlow.getId()))
                    .queryList();
            for (ConflictFlow conflictFlow : conflicts) {
                conflictFlow.insert();
            }
        }
        failedItemFlow.insert();
        return true;
    }

    @Override
    public boolean update(FailedItem object) {
        FailedItemFlow failedItemFlow = null;//FailedItem_Flow.fromModel(object);
        ImportSummaryFlow importSummaryFlow = failedItemFlow.getImportSummary();

        if (importSummaryFlow != null) {
            List<ConflictFlow> conflicts = new Select()
                    .from(ConflictFlow.class)
                    .where(ConflictFlow_Table
                            .importSummary.is(importSummaryFlow.getId()))
                    .queryList();
            for (ConflictFlow conflictFlow : conflicts) {
                conflictFlow.update();
            }
        }
        failedItemFlow.update();
        return true;
    }

    @Override
    public boolean save(FailedItem object) {
        FailedItemFlow failedItemFlow = null;//FailedItem_Flow.fromModel(object);
        ImportSummaryFlow importSummaryFlow = failedItemFlow.getImportSummary();
        if (importSummaryFlow != null) {
            List<ConflictFlow> conflicts = new Select()
                    .from(ConflictFlow.class)
                    .where(ConflictFlow_Table
                            .importSummary.is(importSummaryFlow.getId()))
                    .queryList();
            for (ConflictFlow conflictFlow : conflicts) {
                conflictFlow.save();
            }
        }
        failedItemFlow.save();
        return true;
    }

    @Override
    public boolean delete(FailedItem object) {
        FailedItemFlow failedItemFlow = null;//FailedItem_Flow.fromModel(object);
        ImportSummaryFlow importSummaryFlow = failedItemFlow.getImportSummary();
        if (importSummaryFlow != null) {
            List<ConflictFlow> conflicts = new Select()
                    .from(ConflictFlow.class)
                    .where(ConflictFlow_Table
                            .importSummary.is(importSummaryFlow.getId()))
                    .queryList();
            for (ConflictFlow conflictFlow : conflicts) {
                conflictFlow.delete();
            }
        }
        failedItemFlow.delete();
        return true;
    }

    @Override
    public boolean deleteAll() {
        throw new UnsupportedOperationException("Unimplemented method");
    }

    @Override
    public FailedItem queryById(long id) {
        return null;
    }

    @Override
    public List<FailedItem> queryAll() {
        List<FailedItemFlow> failedItemFlows = new Select()
                .from(FailedItemFlow.class)
                .queryList();
        for (FailedItemFlow failedItemFlow : failedItemFlows) {
            ImportSummaryFlow importSummaryFlow = failedItemFlow.getImportSummary();
            if (importSummaryFlow != null) {
                List<ConflictFlow> conflicts = new Select()
                        .from(ConflictFlow.class)
                        .where(ConflictFlow_Table
                                .importSummary.is(importSummaryFlow.getId()))
                        .queryList();
                importSummaryFlow.setConflicts(conflicts);
            }
        }
        return null;//FailedItem_Flow.toModels(failedItemFlows);
    }

    @Override
    public List<FailedItem> query(FailedItemType type) {
        List<FailedItemFlow> failedItemFlows = new Select()
                .from(FailedItemFlow.class)
                .where(FailedItemFlow_Table
                        .itemType.is(type))
                .queryList();

        for (FailedItemFlow failedItemFlow : failedItemFlows) {
            ImportSummaryFlow importSummaryFlow = failedItemFlow.getImportSummary();

            if (importSummaryFlow != null) {
                List<ConflictFlow> conflicts = new Select()
                        .from(ConflictFlow.class)
                        .where(ConflictFlow_Table
                                .importSummary.is(importSummaryFlow.getId()))
                        .queryList();

                importSummaryFlow.setConflicts(conflicts);
            }
        }

        return null;//FailedItem_Flow.toModels(failedItemFlows);
    }

    @Override
    public FailedItem query(FailedItemType type, long itemId) {
        FailedItemFlow failedItemFlow = new Select()
                .from(FailedItemFlow.class)
                .where(FailedItemFlow_Table.itemType.is(type))
                .and(FailedItemFlow_Table.itemId.is(itemId))
                .querySingle();
        ImportSummaryFlow importSummaryFlow = failedItemFlow.getImportSummary();
        if (importSummaryFlow != null) {
            List<ConflictFlow> conflicts = new Select()
                    .from(ConflictFlow.class)
                    .where(ConflictFlow_Table
                            .importSummary.is(importSummaryFlow.getId()))
                    .queryList();
            importSummaryFlow.setConflicts(conflicts);
        }
        return null;
    }
}
