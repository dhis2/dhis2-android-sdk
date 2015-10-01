package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.importcount.ImportCount;

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
