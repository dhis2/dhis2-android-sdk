package org.hisp.dhis.android.core.organisationunit;

import org.hisp.dhis.android.core.common.BaseNameableObjectContract;

public class OrganisationUnitContract {
    public interface Columns extends BaseNameableObjectContract.Columns {
        String PATH = "path";
        String OPENING_DATE = "openingDate";
        String CLOSED_DATE = "closedDate";
        String PARENT = "parent";
        String LEVEL = "level";
    }
}
