package org.hisp.dhis.android.testapp.category;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class CategoryOptionComboPublicAccessShould extends BasePublicAccessShould<CategoryOptionCombo> {

    @Mock
    private CategoryOptionCombo object;

    @Override
    public CategoryOptionCombo object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        CategoryOptionCombo.create(null);
    }

    @Override
    public void has_public_builder_method() {
        CategoryOptionCombo.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}