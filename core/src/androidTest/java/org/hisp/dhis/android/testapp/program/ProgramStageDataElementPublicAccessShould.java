package org.hisp.dhis.android.testapp.program;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class ProgramStageDataElementPublicAccessShould extends BasePublicAccessShould<ProgramStageDataElement> {

    @Mock
    private ProgramStageDataElement object;

    @Override
    public ProgramStageDataElement object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        ProgramStageDataElement.create(null);
    }

    @Override
    public void has_public_builder_method() {
        ProgramStageDataElement.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}