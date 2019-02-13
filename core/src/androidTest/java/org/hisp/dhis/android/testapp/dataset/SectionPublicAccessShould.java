package org.hisp.dhis.android.testapp.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.dataset.SectionSamples;
import org.hisp.dhis.android.core.dataset.Section;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SectionPublicAccessShould extends BasePublicAccessShould<Section> {

    @Override
    public Section buildObject() {
        return SectionSamples.getSection();
    }

    @Override
    public void has_public_create_method() {
        Section.create(null);
    }

    @Override
    public void has_public_builder_method() {
        Section.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        buildObject().toBuilder();
    }
}