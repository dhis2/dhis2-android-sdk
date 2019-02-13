package org.hisp.dhis.android.testapp.legendset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.legendset.Legend;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class LegendPublicAccessShould extends BasePublicAccessShould<Legend> {

    @Mock
    private Legend object;

    @Override
    public Legend object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        Legend.create(null);
    }

    @Override
    public void has_public_builder_method() {
        Legend.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}