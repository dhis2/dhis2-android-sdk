package org.hisp.dhis.android.testapp.program;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class ProgramTrackedEntityAttributePublicAccessShould
        extends BasePublicAccessShould<ProgramTrackedEntityAttribute> {

    @Mock
    private ProgramTrackedEntityAttribute object;

    @Override
    public ProgramTrackedEntityAttribute object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        ProgramTrackedEntityAttribute.create(null);
    }

    @Override
    public void has_public_builder_method() {
        ProgramTrackedEntityAttribute.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}