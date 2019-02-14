package org.hisp.dhis.android.testapp.organisationunit;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class OrganisationUnitGroupPublicAccessShould extends BasePublicAccessShould<OrganisationUnitGroup> {

    @Mock
    private OrganisationUnitGroup object;

    @Override
    public OrganisationUnitGroup object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        OrganisationUnitGroup.create(null);
    }

    @Override
    public void has_public_builder_method() {
        OrganisationUnitGroup.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}