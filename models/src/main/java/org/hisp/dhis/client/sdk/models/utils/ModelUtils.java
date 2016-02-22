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

package org.hisp.dhis.client.sdk.models.utils;

import org.hisp.dhis.client.sdk.models.common.MergeStrategy;
import org.hisp.dhis.client.sdk.models.common.base.IdentifiableObject;

import java.util.*;

public class ModelUtils implements IModelUtils {
    public ModelUtils() {
        // private constructor
    }

    @Override
    public <T extends IdentifiableObject> Map<String, T> toMap(Collection<T> objects) {
        Map<String, T> map = new HashMap<>();
        if (objects != null && objects.size() > 0) {
            for (T object : objects) {
                if (object.getUId() != null) {
                    map.put(object.getUId(), object);
                }
            }
        }
        return map;
    }

    @Override
    public <T extends IdentifiableObject> List<String> toUidList(List<T> objects) {
        List<String> ids = new ArrayList<>();
        if (objects != null && objects.size() > 0) {
            for (T object : objects) {
                ids.add(object.getUId());
            }
        }
        return ids;
    }

    @Override
    public <T extends IdentifiableObject> Set<String> toUidSet(Collection<T> items) {
        Set<String> uIds = new HashSet<>();

        if (items != null && !items.isEmpty()) {
            for (T item : items) {
                uIds.add(item.getUId());
            }
        }

        return uIds;
    }

    /**
     * Returns a list of items taken from updatedItems and persistedItems, based on the items in
     * the passed existingItems List. Items that are not present in existingItems will not be
     * included.
     */
    @Override
    public <T extends IdentifiableObject> List<T> merge(List<T> existingItems, List<T> updatedItems,
                                                        List<T> persistedItems) {
        Map<String, T> updatedItemsMap = toMap(updatedItems);
        Map<String, T> persistedItemsMap = toMap(persistedItems);
        Map<String, T> existingItemsMap = new HashMap<>();

        if (existingItems == null || existingItems.isEmpty()) {
            return new ArrayList<>(existingItemsMap.values());
        }

        for (T existingItem : existingItems) {
            String id = existingItem.getUId();
            T updatedItem = updatedItemsMap.get(id);
            T persistedItem = persistedItemsMap.get(id);

            if (updatedItem != null) {
                if (persistedItem != null) {
                    updatedItem.setId(persistedItem.getId());
                }
                existingItemsMap.put(id, updatedItem);
                continue;
            }

            if (persistedItem != null) {
                existingItemsMap.put(id, persistedItem);
            }
        }

        return new ArrayList<>(existingItemsMap.values());
    }

    @Override
    public <T extends IdentifiableObject> List<T> mergeWith(Collection<T> one, Collection<T> two,
                                                            MergeStrategy strategy) {
        Map<String, T> collectionOneMap = toMap(one);
        Map<String, T> collectionTwoMap = toMap(two);

        for (String uid : collectionOneMap.keySet()) {
            T itemOne = collectionOneMap.get(uid);
            T itemTwo = collectionTwoMap.get(uid);

            if (itemTwo != null) {
                itemOne.mergeWith(itemTwo, strategy);
            }
        }

        return new ArrayList<>(collectionOneMap.values());
    }
}
