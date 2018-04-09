package org.hisp.dhis.android.core.category;

import org.hisp.dhis.android.core.common.IdentifiableHandlerImpl;
import org.junit.Test;

public class CategoryOptionHandlerShould {

    @Test
    public void extend_identifiable_handler_impl() {
        IdentifiableHandlerImpl<CategoryOption, CategoryOptionModel> categoryOptionHandler =
                new CategoryOptionHandler(null);
    }
}