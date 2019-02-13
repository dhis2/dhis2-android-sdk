package org.hisp.dhis.android.testapp.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.dataset.DataSetSamples;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DataSetPublicAccessShould extends BasePublicAccessShould<DataSet> {

    @Override
    public DataSet buildObject() {
        return DataSetSamples.getDataSet();
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
        buildObject().toBuilder();
    }
}