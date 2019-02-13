package org.hisp.dhis.android.testapp.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.dataset.DataInputPeriodSamples;
import org.hisp.dhis.android.core.dataset.DataInputPeriod;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DataInputPeriodPublicAccessShould extends BasePublicAccessShould<DataInputPeriod> {

    @Override
    public DataInputPeriod buildObject() {
        return DataInputPeriodSamples.getDataInputPeriod();
    }

    @Override
    public void has_public_create_method() {
        DataInputPeriod.create(null);
    }

    @Override
    public void has_public_builder_method() {
        DataInputPeriod.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        buildObject().toBuilder();
    }
}