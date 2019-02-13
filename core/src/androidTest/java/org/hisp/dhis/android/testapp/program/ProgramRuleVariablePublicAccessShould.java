package org.hisp.dhis.android.testapp.program;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class ProgramRuleVariablePublicAccessShould extends BasePublicAccessShould<ProgramRuleVariable> {

    @Mock
    private ProgramRuleVariable object;

    @Override
    public ProgramRuleVariable object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        ProgramRuleVariable.create(null);
    }

    @Override
    public void has_public_builder_method() {
        ProgramRuleVariable.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}