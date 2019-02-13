package org.hisp.dhis.android.testapp.option;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class OptionSetPublicAccessShould extends BasePublicAccessShould<OptionSet> {

    @Mock
    private OptionSet object;

    @Override
    public OptionSet object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        OptionSet.create(null);
    }

    @Override
    public void has_public_builder_method() {
        OptionSet.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}