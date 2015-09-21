package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.sdk.models.program.Program;
import org.hisp.dhis.android.sdk.models.programindicator.ProgramIndicator;
import org.hisp.dhis.android.sdk.models.programstagesection.ProgramStageSection;

@Table(databaseName = DbDhis.NAME)
public final class OrganisationUnitToProgramRelation$Flow extends BaseModel {

    private final static String ORGANISATION_UNIT_KEY = "organisationUnit";
    private final static String PROGRAM_KEY = "program";

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = ORGANISATION_UNIT_KEY, columnType = String.class, foreignColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    OrganisationUnit$Flow organisationUnit;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = PROGRAM_KEY, columnType = String.class, foreignColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    Program$Flow program;

    public OrganisationUnitToProgramRelation$Flow() {
        //empty constructor
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OrganisationUnit$Flow getOrganisationUnit() {
        return organisationUnit;
    }

    public void setOrganisationUnit(OrganisationUnit$Flow organisationUnit) {
        this.organisationUnit = organisationUnit;
    }

    public Program$Flow getProgram() {
        return program;
    }

    public void setProgram(Program$Flow program) {
        this.program = program;
    }
}
