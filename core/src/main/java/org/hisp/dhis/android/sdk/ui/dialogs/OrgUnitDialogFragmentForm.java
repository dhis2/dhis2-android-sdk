package org.hisp.dhis.android.sdk.ui.dialogs;

import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.utils.api.ProgramType;

import java.util.List;

public class OrgUnitDialogFragmentForm {
    private ProgramType[] programTypes;
    private List<AutoCompleteDialogAdapter.OptionAdapterValue> optionAdapterValueList;
    private List<OrganisationUnit> organisationUnits;
    private Error type;


    public ProgramType[] getProgramTypes() {
        return programTypes;
    }

    public void setProgramTypes(ProgramType[] programTypes) {
        this.programTypes = programTypes;
    }

    public List<AutoCompleteDialogAdapter.OptionAdapterValue> getOptionAdapterValueList() {
        return optionAdapterValueList;
    }

    public void setOptionAdapterValueList(List<AutoCompleteDialogAdapter.OptionAdapterValue> optionAdapterValueList) {
        this.optionAdapterValueList = optionAdapterValueList;
    }

    public List<OrganisationUnit> getOrganisationUnits() {
        return organisationUnits;
    }

    public void setOrganisationUnits(List<OrganisationUnit> organisationUnits) {
        this.organisationUnits = organisationUnits;
    }

    public Error getType() {
        return type;
    }

    public void setType(Error type) {
        this.type = type;
    }

    public enum Error {
        NONE,
        NO_ASSIGNED_ORGANISATION_UNITS,
        NO_PROGRAMS_TO_ORGANSATION_UNIT
    }

}
