package org.hisp.dhis.android.testapp.common;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class ObjectStylePublicAccessShould extends BasePublicAccessShould<ObjectStyle> {

    @Mock
    private ObjectStyle object;

    @Override
    public ObjectStyle object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        ObjectStyle.create(null);
    }

    @Override
    public void has_public_builder_method() {
        ObjectStyle.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}