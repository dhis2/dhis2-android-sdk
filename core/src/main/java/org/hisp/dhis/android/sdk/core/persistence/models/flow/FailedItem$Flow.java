package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.constant.Constant;
import org.hisp.dhis.android.sdk.models.faileditem.FailedItem;

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
        failedItem.setItemType(failedItemFlow.getItemType());
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
        failedItemFlow.setItemType(failedItem.getItemType());
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
