package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.programindicator.ProgramIndicator;
import org.hisp.dhis.android.sdk.models.programstagesection.ProgramStageSection;

@Table(databaseName = DbDhis.NAME)
public final class ProgramIndicatorToProgramStageSectionRelation$Flow extends BaseModel {

    private final static String PROGRAM_INDICATOR_KEY = "programIndicator";
    private final static String PROGRAM_STAGE_SECTION_KEY = "programStageSection";

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = PROGRAM_INDICATOR_KEY, columnType = String.class, foreignColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    ProgramIndicator$Flow programIndicator;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = PROGRAM_STAGE_SECTION_KEY, columnType = String.class, foreignColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    ProgramStageSection$Flow programStageSection;

    public ProgramIndicatorToProgramStageSectionRelation$Flow() {
        //empty constructor
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProgramIndicator$Flow getProgramIndicator() {
        return programIndicator;
    }

    public void setProgramIndicator(ProgramIndicator$Flow programIndicator) {
        this.programIndicator = programIndicator;
    }

    public ProgramStageSection$Flow getProgramStageSection() {
        return programStageSection;
    }

    public void setProgramStageSection(ProgramStageSection$Flow programStageSection) {
        this.programStageSection = programStageSection;
    }
}
