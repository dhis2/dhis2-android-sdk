package org.hisp.dhis.android.testapp.dataelement;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class DataElementPublicAccessShould extends BasePublicAccessShould<DataElement> {

    @Mock
    private DataElement object;

    @Override
    public DataElement object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        DataElement.create(null);
    }

    @Override
    public void has_public_builder_method() {
        DataElement.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}