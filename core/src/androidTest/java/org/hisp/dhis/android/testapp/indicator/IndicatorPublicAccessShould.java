package org.hisp.dhis.android.testapp.indicator;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.indicator.Indicator;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class IndicatorPublicAccessShould extends BasePublicAccessShould<Indicator> {

    @Mock
    private Indicator object;

    @Override
    public Indicator object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        Indicator.create(null);
    }

    @Override
    public void has_public_builder_method() {
        Indicator.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}