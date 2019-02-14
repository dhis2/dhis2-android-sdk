package org.hisp.dhis.android.testapp.dataelement;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class DataElementOperandPublicAccessShould extends BasePublicAccessShould<DataElementOperand> {

    @Mock
    private DataElementOperand object;

    @Override
    public DataElementOperand object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        DataElementOperand.create(null);
    }

    @Override
    public void has_public_builder_method() {
        DataElementOperand.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}