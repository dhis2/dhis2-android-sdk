package org.hisp.dhis.android.testapp.program;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class ProgramPublicAccessShould extends BasePublicAccessShould<Program> {

    @Mock
    private Program object;

    @Override
    public Program object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        Program.create(null);
    }

    @Override
    public void has_public_builder_method() {
        Program.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}