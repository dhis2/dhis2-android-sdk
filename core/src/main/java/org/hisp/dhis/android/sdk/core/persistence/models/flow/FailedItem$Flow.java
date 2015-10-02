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

package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.common.faileditem.FailedItem;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class FailedItem$Flow extends BaseModel {

    static final int UNIQUE_FAILEDITEM_GROUP = 123493;
    static final String IMPORTSUMMARY_KEY = "importSummary";

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = IMPORTSUMMARY_KEY, columnType = long.class, foreignColumnName = "id"),
            }, saveForeignKeyModel = false
    )
    ImportSummary$Flow importSummary;

    @Column
    @Unique(unique = true, uniqueGroups = {UNIQUE_FAILEDITEM_GROUP})
    long itemId;

    @Column
    @Unique(unique = true, uniqueGroups = {UNIQUE_FAILEDITEM_GROUP})
    FailedItem.Type itemType;

    @Column
    int httpStatusCode;

    @Column
    String errorMessage;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ImportSummary$Flow getImportSummary() {
        return importSummary;
    }

    public void setImportSummary(ImportSummary$Flow importSummary) {
        this.importSummary = importSummary;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public FailedItem.Type getItemType() {
        return itemType;
    }

    public void setItemType(FailedItem.Type itemType) {
        this.itemType = itemType;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public FailedItem$Flow() {
        // empty constructor
    }

    public static FailedItem toModel(FailedItem$Flow failedItemFlow) {
        if (failedItemFlow == null) {
            return null;
        }

        FailedItem failedItem = new FailedItem();
        failedItem.setId(failedItemFlow.getId());
        failedItem.setErrorMessage(failedItemFlow.getErrorMessage());
        failedItem.setHttpStatusCode(failedItemFlow.getHttpStatusCode());
        failedItem.setImportSummary(ImportSummary$Flow.toModel(failedItemFlow.getImportSummary()));
        failedItem.setItemId(failedItemFlow.getItemId());
        failedItem.setItemFailedItemType(failedItemFlow.getItemType());
        return failedItem;
    }

    public static FailedItem$Flow fromModel(FailedItem failedItem) {
        if (failedItem == null) {
            return null;
        }

        FailedItem$Flow failedItemFlow = new FailedItem$Flow();
        failedItemFlow.setId(failedItem.getId());
        failedItemFlow.setErrorMessage(failedItem.getErrorMessage());
        failedItemFlow.setHttpStatusCode(failedItem.getHttpStatusCode());
        failedItemFlow.setImportSummary(ImportSummary$Flow.fromModel(failedItem.getImportSummary()));
        failedItemFlow.setItemId(failedItem.getItemId());
        failedItemFlow.setItemType(failedItem.getItemFailedItemType());
        return failedItemFlow;
    }

    public static List<FailedItem> toModels(List<FailedItem$Flow> failedItemFlows) {
        List<FailedItem> failedItems = new ArrayList<>();

        if (failedItemFlows != null && !failedItemFlows.isEmpty()) {
            for (FailedItem$Flow failedItemFlow : failedItemFlows) {
                failedItems.add(toModel(failedItemFlow));
            }
        }

        return failedItems;
    }

    public static List<FailedItem$Flow> fromModels(List<FailedItem> failedItems) {
        List<FailedItem$Flow> failedItemFlows = new ArrayList<>();

        if (failedItems != null && !failedItems.isEmpty()) {
            for (FailedItem failedItem : failedItems) {
                failedItemFlows.add(fromModel(failedItem));
            }
        }

        return failedItemFlows;
    }
}
