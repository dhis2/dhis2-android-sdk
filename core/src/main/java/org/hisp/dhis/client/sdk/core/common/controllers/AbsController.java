package org.hisp.dhis.client.sdk.core.common.controllers;

import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.IStore;
import org.hisp.dhis.client.sdk.models.common.base.IdentifiableObject;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbsController<T extends IdentifiableObject> {

    protected List<IDbOperation> getMergeOperations(List<T> existingItems, List<T> updatedItems,
                                                    List<T> persistedItems, IStore<T> store) {
        Map<String, T> persistedItemsMap = ModelUtils.toMap(persistedItems);
        Map<String, T> updatedItemsMap = ModelUtils.toMap(updatedItems);
        Map<String, T> existingItemsMap = ModelUtils.toMap(existingItems);

        for (T persistedOrganisationUnit : persistedItems) {
            T updatedOrganisationUnit = updatedItemsMap.get(persistedOrganisationUnit.getUId());
            if (updatedOrganisationUnit != null) {
                updatedOrganisationUnit.setId(persistedOrganisationUnit.getId());
            }
        }

        List<IDbOperation> operations = new ArrayList<>();
        for (T updatedItem : updatedItems) {
            if (persistedItemsMap.containsKey(updatedItem.getUId())) {
                operations.add(DbOperation.with(store).update(updatedItem));
            } else {
                operations.add(DbOperation.with(store).insert(updatedItem));
            }
        }

        for (String persistedItemUid : persistedItemsMap.keySet()) {
            if (!existingItemsMap.containsKey(persistedItemUid)) {
                operations.add(DbOperation.with(store).delete(persistedItemsMap.get
                        (persistedItemUid)));
            }
        }

        return operations;
    }

}
