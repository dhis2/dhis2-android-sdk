package org.hisp.dhis.android.testapp.category;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class CategoryOptionPublicAccessShould extends BasePublicAccessShould<CategoryOption> {

    @Mock
    private CategoryOption object;

    @Override
    public CategoryOption object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        CategoryOption.create(null);
    }

    @Override
    public void has_public_builder_method() {
        CategoryOption.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}