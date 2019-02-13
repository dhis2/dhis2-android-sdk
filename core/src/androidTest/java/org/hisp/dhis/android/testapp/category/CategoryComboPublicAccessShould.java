package org.hisp.dhis.android.testapp.category;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class CategoryComboPublicAccessShould extends BasePublicAccessShould<CategoryCombo> {

    @Mock
    private CategoryCombo object;

    @Override
    public CategoryCombo object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        CategoryCombo.create(null);
    }

    @Override
    public void has_public_builder_method() {
        CategoryCombo.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}