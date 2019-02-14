package org.hisp.dhis.android.testapp.maintenance;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class D2ErrorPublicAccessShould extends BasePublicAccessShould<D2Error> {

    @Mock
    private D2Error object;

    @Override
    public D2Error object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        D2Error.create(null);
    }

    @Override
    public void has_public_builder_method() {
        D2Error.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}