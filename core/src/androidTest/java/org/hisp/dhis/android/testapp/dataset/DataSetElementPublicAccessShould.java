package org.hisp.dhis.android.testapp.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.dataset.DataSetElement;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class DataSetElementPublicAccessShould extends BasePublicAccessShould<DataSetElement> {

    @Mock
    private DataSetElement object;

    @Override
    public DataSetElement object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        DataSetElement.create(null);
    }

    @Override
    public void has_public_builder_method() {
        DataSetElement.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}