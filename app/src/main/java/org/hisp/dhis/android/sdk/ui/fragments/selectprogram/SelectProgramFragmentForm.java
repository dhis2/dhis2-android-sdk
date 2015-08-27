package org.hisp.dhis.android.sdk.ui.fragments.selectprogram;

import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.EventRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.TrackedEntityInstanceColumnNamesRow;

import java.util.List;

/**
 * Created by erling on 8/25/15.
 */
public class SelectProgramFragmentForm
{
    private List<EventRow> eventRowList;
    private Program program;
    private OrganisationUnit orgUnit;
    private TrackedEntityInstanceColumnNamesRow columnNames;

    public SelectProgramFragmentForm(){}

    public List<EventRow> getEventRowList() {
        return eventRowList;
    }

    public void setEventRowList(List<EventRow> eventRowList) {
        this.eventRowList = eventRowList;
    }

    public OrganisationUnit getOrgUnit() {
        return orgUnit;
    }

    public void setOrgUnit(OrganisationUnit orgUnit) {
        this.orgUnit = orgUnit;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public TrackedEntityInstanceColumnNamesRow getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(TrackedEntityInstanceColumnNamesRow columnNames) {
        this.columnNames = columnNames;
    }
}
