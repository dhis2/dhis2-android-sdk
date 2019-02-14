package org.hisp.dhis.android.testapp.program;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class ProgramStagePublicAccessShould extends BasePublicAccessShould<ProgramStage> {

    @Mock
    private ProgramStage object;

    @Override
    public ProgramStage object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        ProgramStage.create(null);
    }

    @Override
    public void has_public_builder_method() {
        ProgramStage.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}