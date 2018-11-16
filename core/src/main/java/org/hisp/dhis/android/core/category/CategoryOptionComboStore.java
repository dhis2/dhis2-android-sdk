package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.common.IdentifiableObjectStore;

import java.util.List;

interface CategoryOptionComboStore extends IdentifiableObjectStore<CategoryOptionCombo> {
    List<CategoryOptionCombo> getForCategoryCombo(String categoryComboUid);
}
