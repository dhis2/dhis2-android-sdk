package org.hisp.dhis.android.testapp.program;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class ProgramRulePublicAccessShould extends BasePublicAccessShould<ProgramRule> {

    @Mock
    private ProgramRule object;

    @Override
    public ProgramRule object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        ProgramRule.create(null);
    }

    @Override
    public void has_public_builder_method() {
        ProgramRule.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}