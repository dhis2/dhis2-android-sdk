package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.metadata.OrganisationUnit;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class OrganisationUnit$Flow extends BaseIdentifiableObject$Flow {

    @Column
    String label;

    @Column
    int level;

    @Column
    String parent;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public OrganisationUnit$Flow() {
        // empty constructor
    }

    public static OrganisationUnit toModel(OrganisationUnit$Flow organisationUnitFlow) {
        if (organisationUnitFlow == null) {
            return null;
        }

        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setId(organisationUnitFlow.getId());
        organisationUnit.setUId(organisationUnitFlow.getUId());
        organisationUnit.setCreated(organisationUnitFlow.getCreated());
        organisationUnit.setLastUpdated(organisationUnitFlow.getLastUpdated());
        organisationUnit.setName(organisationUnitFlow.getName());
        organisationUnit.setDisplayName(organisationUnitFlow.getDisplayName());
        organisationUnit.setAccess(organisationUnitFlow.getAccess());
        organisationUnit.setLabel(organisationUnitFlow.getLabel());
        organisationUnit.setLevel(organisationUnitFlow.getLevel());
        organisationUnit.setParent(organisationUnitFlow.getParent());
        return organisationUnit;
    }

    public static OrganisationUnit$Flow fromModel(OrganisationUnit organisationUnit) {
        if (organisationUnit == null) {
            return null;
        }

        OrganisationUnit$Flow organisationUnitFlow = new OrganisationUnit$Flow();
        organisationUnitFlow.setId(organisationUnit.getId());
        organisationUnitFlow.setUId(organisationUnit.getUId());
        organisationUnitFlow.setCreated(organisationUnit.getCreated());
        organisationUnitFlow.setLastUpdated(organisationUnit.getLastUpdated());
        organisationUnitFlow.setName(organisationUnit.getName());
        organisationUnitFlow.setDisplayName(organisationUnit.getDisplayName());
        organisationUnitFlow.setAccess(organisationUnit.getAccess());
        organisationUnitFlow.setLabel(organisationUnit.getLabel());
        organisationUnitFlow.setLevel(organisationUnit.getLevel());
        organisationUnitFlow.setParent(organisationUnit.getParent());
        return organisationUnitFlow;
    }

    public static List<OrganisationUnit> toModels(List<OrganisationUnit$Flow> organisationUnitFlows) {
        List<OrganisationUnit> organisationUnits = new ArrayList<>();

        if (organisationUnitFlows != null && !organisationUnitFlows.isEmpty()) {
            for (OrganisationUnit$Flow organisationUnitFlow : organisationUnitFlows) {
                organisationUnits.add(toModel(organisationUnitFlow));
            }
        }

        return organisationUnits;
    }

    public static List<OrganisationUnit$Flow> fromModels(List<OrganisationUnit> organisationUnits) {
        List<OrganisationUnit$Flow> organisationUnitFlows = new ArrayList<>();

        if (organisationUnitFlows != null && !organisationUnitFlows.isEmpty()) {
            for (OrganisationUnit organisationUnit : organisationUnits) {
                organisationUnitFlows.add(fromModel(organisationUnit));
            }
        }

        return organisationUnitFlows;
    }
}
