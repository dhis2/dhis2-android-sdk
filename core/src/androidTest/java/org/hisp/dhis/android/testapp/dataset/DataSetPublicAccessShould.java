package org.hisp.dhis.android.testapp.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class DataSetPublicAccessShould extends BasePublicAccessShould<DataSet> {

    @Mock
    private DataSet object;

    @Override
    public DataSet object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        DataSet.create(null);
    }

    @Override
    public void has_public_builder_method() {
        DataSet.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}