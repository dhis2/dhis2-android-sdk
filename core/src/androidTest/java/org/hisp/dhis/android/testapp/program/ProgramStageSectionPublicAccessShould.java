package org.hisp.dhis.android.testapp.program;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.program.ProgramStageSection;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class ProgramStageSectionPublicAccessShould extends BasePublicAccessShould<ProgramStageSection> {

    @Mock
    private ProgramStageSection object;

    @Override
    public ProgramStageSection object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        ProgramStageSection.create(null);
    }

    @Override
    public void has_public_builder_method() {
        ProgramStageSection.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}