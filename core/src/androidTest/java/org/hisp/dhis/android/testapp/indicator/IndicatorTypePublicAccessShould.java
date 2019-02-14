package org.hisp.dhis.android.testapp.indicator;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.indicator.IndicatorType;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class IndicatorTypePublicAccessShould extends BasePublicAccessShould<IndicatorType> {

    @Mock
    private IndicatorType object;

    @Override
    public IndicatorType object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        IndicatorType.create(null);
    }

    @Override
    public void has_public_builder_method() {
        IndicatorType.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}