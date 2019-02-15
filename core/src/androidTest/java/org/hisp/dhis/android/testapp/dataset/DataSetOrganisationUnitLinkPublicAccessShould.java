package org.hisp.dhis.android.testapp.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLink;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class DataSetOrganisationUnitLinkPublicAccessShould extends BasePublicAccessShould<DataSetOrganisationUnitLink> {

    @Mock
    private DataSetOrganisationUnitLink object;

    @Override
    public DataSetOrganisationUnitLink object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        DataSetOrganisationUnitLink.create(null);
    }

    @Override
    public void has_public_builder_method() {
        DataSetOrganisationUnitLink.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}