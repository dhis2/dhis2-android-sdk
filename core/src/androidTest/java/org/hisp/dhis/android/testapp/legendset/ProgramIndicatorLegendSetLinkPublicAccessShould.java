package org.hisp.dhis.android.testapp.legendset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLink;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class ProgramIndicatorLegendSetLinkPublicAccessShould
        extends BasePublicAccessShould<ProgramIndicatorLegendSetLink> {

    @Mock
    private ProgramIndicatorLegendSetLink object;

    @Override
    public ProgramIndicatorLegendSetLink object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        ProgramIndicatorLegendSetLink.create(null);
    }

    @Override
    public void has_public_builder_method() {
        ProgramIndicatorLegendSetLink.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}