package org.hisp.dhis.android.testapp.organisationunit;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class OrganisationUnitLevelPublicAccessShould extends BasePublicAccessShould<OrganisationUnitLevel> {

    @Mock
    private OrganisationUnitLevel object;

    @Override
    public OrganisationUnitLevel object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        OrganisationUnitLevel.create(null);
    }

    @Override
    public void has_public_builder_method() {
        OrganisationUnitLevel.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}