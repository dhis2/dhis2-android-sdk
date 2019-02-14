package org.hisp.dhis.android.testapp.indicator;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.indicator.DataSetIndicatorLink;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class DataSetIndicatorLinkPublicAccessShould extends BasePublicAccessShould<DataSetIndicatorLink> {

    @Mock
    private DataSetIndicatorLink object;

    @Override
    public DataSetIndicatorLink object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        DataSetIndicatorLink.create(null);
    }

    @Override
    public void has_public_builder_method() {
        DataSetIndicatorLink.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}