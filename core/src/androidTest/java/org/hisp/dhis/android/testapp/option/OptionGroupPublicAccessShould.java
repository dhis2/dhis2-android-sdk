package org.hisp.dhis.android.testapp.option;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.option.OptionGroup;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class OptionGroupPublicAccessShould extends BasePublicAccessShould<OptionGroup> {

    @Mock
    private OptionGroup object;

    @Override
    public OptionGroup object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        OptionGroup.create(null);
    }

    @Override
    public void has_public_builder_method() {
        OptionGroup.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}