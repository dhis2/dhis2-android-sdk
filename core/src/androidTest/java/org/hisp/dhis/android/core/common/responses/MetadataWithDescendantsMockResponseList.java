package org.hisp.dhis.android.core.common.responses;

import java.io.IOException;

public class MetadataWithDescendantsMockResponseList extends BasicMetadataMockResponseList {

    public MetadataWithDescendantsMockResponseList() throws IOException {
        super();
    }

    @Override
    protected String getUserMockResponse() {
        return "admin/user.json";
    }

    @Override
    protected String getOrganisationUnitMockResponse() {
        return "admin/organisation_units.json";
    }

}
