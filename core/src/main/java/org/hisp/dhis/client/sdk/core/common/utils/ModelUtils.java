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

package org.hisp.dhis.client.sdk.core.common.utils;

import org.hisp.dhis.client.sdk.models.common.base.IdentifiableObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class ModelUtils {
    private ModelUtils() {
        // private constructor
    }

    public static <T extends IdentifiableObject> Map<String, T> toMap(Collection<T> objects) {
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

    public static <T extends IdentifiableObject> List<String> toUidList(List<T> objects) {
        List<String> ids = new ArrayList<>();
        if (objects != null && objects.size() > 0) {
            for (T object : objects) {
                ids.add(object.getUId());
            }
        }
        return ids;
    }

    public static <T extends IdentifiableObject> Set<String> toUidSet(Collection<T> items) {
        Set<String> uIds = new HashSet<>();

        if (items != null && !items.isEmpty()) {
            for (T item : items) {
                uIds.add(item.getUId());
            }
        }

        return uIds;
    }

    public static <T extends IdentifiableObject> Set<String> toUidSet(
            Collection<T> items, ModelAction<T> action) {
        isNull(action, "ModelAction must not be null");

        Set<String> uIds = new HashSet<>();
        if (items != null && !items.isEmpty()) {
            for (T item : items) {
                uIds.addAll(action.getUids(item));
            }
        }

        return uIds;
    }

    public interface ModelAction<T extends IdentifiableObject> {
        Collection<String> getUids(T model);
    }

    /**
     * Returns a list of items taken from updatedItems and persistedItems, based on the items in
     * the passed existingItems List. Items that are not present in existingItems will not be
     * included.
     */
    public static <T extends IdentifiableObject> List<T> merge(List<T> existingItems,
                                                               List<T> updatedItems,
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

    public static <T> List<T> asList(T... items) {
        if (items == null || items.length == 0) {
            return new ArrayList<>();
        }

        return Arrays.asList(items);
    }
}
