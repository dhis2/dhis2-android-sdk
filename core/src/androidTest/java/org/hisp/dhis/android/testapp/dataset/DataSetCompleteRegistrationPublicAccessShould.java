package org.hisp.dhis.android.testapp.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class DataSetCompleteRegistrationPublicAccessShould extends BasePublicAccessShould<DataSetCompleteRegistration> {

    @Mock
    private DataSetCompleteRegistration dataSetCompleteRegistration;

    @Override
    public DataSetCompleteRegistration object() {
        return dataSetCompleteRegistration;
    }

    @Override
    public void has_public_create_method() {
        DataSetCompleteRegistration.create(null);
    }

    @Override
    public void has_public_builder_method() {
        DataSetCompleteRegistration.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}