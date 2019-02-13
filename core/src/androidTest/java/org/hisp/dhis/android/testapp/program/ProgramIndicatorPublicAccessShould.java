package org.hisp.dhis.android.testapp.program;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class ProgramIndicatorPublicAccessShould extends BasePublicAccessShould<ProgramIndicator> {

    @Mock
    private ProgramIndicator object;

    @Override
    public ProgramIndicator object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        ProgramIndicator.create(null);
    }

    @Override
    public void has_public_builder_method() {
        ProgramIndicator.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}