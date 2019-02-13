package org.hisp.dhis.android.testapp.option;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class OptionPublicAccessShould extends BasePublicAccessShould<Option> {

    @Mock
    private Option object;

    @Override
    public Option object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        Option.create(null);
    }

    @Override
    public void has_public_builder_method() {
        Option.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}