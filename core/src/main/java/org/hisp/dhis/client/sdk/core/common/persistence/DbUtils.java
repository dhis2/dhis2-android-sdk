/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.core.common.persistence;

import org.hisp.dhis.client.sdk.models.common.base.IdentifiableObject;
import org.hisp.dhis.client.sdk.models.utils.IModelUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbUtils {

    private DbUtils() {
        // no instances
    }

    public static <T extends IdentifiableObject> List<IDbOperation> createOperations(
            List<T> existingItems, List<T> updatedItems, List<T> persistedItems,
                    IStore<T> store, IModelUtils modelUtils) {
        Map<String, T> persistedItemsMap = modelUtils.toMap(persistedItems);
        Map<String, T> updatedItemsMap = modelUtils.toMap(updatedItems);
        Map<String, T> existingItemsMap = modelUtils.toMap(existingItems);

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
