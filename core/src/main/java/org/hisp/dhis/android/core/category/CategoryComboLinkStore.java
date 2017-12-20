package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.common.DeletableStore;

public interface CategoryComboLinkStore extends DeletableStore {
    long insert(CategoryComboLinkModel element);
}
