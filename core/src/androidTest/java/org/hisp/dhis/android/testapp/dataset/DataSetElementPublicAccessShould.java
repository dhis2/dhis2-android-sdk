package org.hisp.dhis.android.testapp.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.dataset.DataSetElementSamples;
import org.hisp.dhis.android.core.dataset.DataSetElement;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DataSetElementPublicAccessShould extends BasePublicAccessShould<DataSetElement> {

    @Override
    public DataSetElement buildObject() {
        return DataSetElementSamples.getDataSetElement();
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
        buildObject().toBuilder();
    }
}