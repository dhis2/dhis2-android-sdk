package org.hisp.dhis.android.testapp.category;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class CategoryPublicAccessShould extends BasePublicAccessShould<Category> {

    @Mock
    private Category object;

    @Override
    public Category object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        Category.create(null);
    }

    @Override
    public void has_public_builder_method() {
        Category.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}