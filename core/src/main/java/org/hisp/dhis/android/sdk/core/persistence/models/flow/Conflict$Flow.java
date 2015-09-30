package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.conflict.Conflict;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class Conflict$Flow extends BaseModel {

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
    String object;

    @Column
    String value;

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
