package org.hisp.dhis.android.testapp.datavalue;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class DataValuePublicAccessShould extends BasePublicAccessShould<DataValue> {

    @Mock
    private DataValue object;

    @Override
    public DataValue object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        DataValue.create(null);
    }

    @Override
    public void has_public_builder_method() {
        DataValue.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}