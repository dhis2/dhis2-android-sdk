package org.hisp.dhis.android.testapp.organisationunit;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class OrganisationUnitPublicAccessShould extends BasePublicAccessShould<OrganisationUnit> {

    @Mock
    private OrganisationUnit object;

    @Override
    public OrganisationUnit object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        OrganisationUnit.create(null);
    }

    @Override
    public void has_public_builder_method() {
        OrganisationUnit.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}