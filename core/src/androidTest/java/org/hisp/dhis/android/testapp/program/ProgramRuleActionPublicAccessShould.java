package org.hisp.dhis.android.testapp.program;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class ProgramRuleActionPublicAccessShould extends BasePublicAccessShould<ProgramRuleAction> {

    @Mock
    private ProgramRuleAction object;

    @Override
    public ProgramRuleAction object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        ProgramRuleAction.create(null);
    }

    @Override
    public void has_public_builder_method() {
        ProgramRuleAction.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}