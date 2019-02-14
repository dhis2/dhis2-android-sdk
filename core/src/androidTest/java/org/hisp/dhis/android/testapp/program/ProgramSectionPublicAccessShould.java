package org.hisp.dhis.android.testapp.program;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.program.ProgramSection;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class ProgramSectionPublicAccessShould extends BasePublicAccessShould<ProgramSection> {

    @Mock
    private ProgramSection object;

    @Override
    public ProgramSection object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        ProgramSection.create(null);
    }

    @Override
    public void has_public_builder_method() {
        ProgramSection.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}