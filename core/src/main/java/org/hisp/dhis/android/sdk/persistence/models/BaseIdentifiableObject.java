/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Simen Skogly Russnes on 18.02.15.
 */
public abstract class BaseIdentifiableObject extends BaseModel {

    public BaseIdentifiableObject() {

    }

    public BaseIdentifiableObject(BaseIdentifiableObject baseIdentifiableObject) {
        this.name = baseIdentifiableObject.name;
        this.displayName = baseIdentifiableObject.displayName;
        this.created = baseIdentifiableObject.created;
        this.lastUpdated = baseIdentifiableObject.lastUpdated;
        if(baseIdentifiableObject.access != null) {
            this.access = new Access(baseIdentifiableObject.access);
        }
    }

    @JsonProperty("name")
    @Column(name = "name")
    String name;

    @JsonProperty("displayName")
    @Column(name = "displayName")
    String displayName;

    @JsonProperty("created")
    //@JsonIgnore
    @Column(name = "created")
    String created;

    @JsonProperty("lastUpdated")
    //@JsonIgnore
    @Column(name = "lastUpdated")
    String lastUpdated;

    @JsonProperty("access")
    @Column(name = "access")
    Access access;

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
    }


    @JsonProperty("createdAtClient")
    public void setCreatedAtClient(String created) {
        this.created = created;
    }

    @JsonProperty("lastUpdatedAtClient")
    public void setLastUpdatedAtClient(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public abstract String getUid();

    public abstract void setUid(String id);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public static <T extends BaseIdentifiableObject> List<T> merge(List<T> existingItems,
                                                                   List<T> updatedItems,
                                                                   List<T> persistedItems) {
        Map<String, T> updatedItemsMap = toMap(updatedItems);
        Map<String, T> persistedItemsMap = toMap(persistedItems);
        Map<String, T> existingItemsMap = new HashMap<>();

        if (existingItems == null || existingItems.isEmpty()) {
            return new ArrayList<>(existingItemsMap.values());
        }

        for (T existingItem : existingItems) {
            String id = existingItem.getUid();
            T updatedItem = updatedItemsMap.get(id);
            T persistedItem = persistedItemsMap.get(id);

            if (updatedItem != null) {
                if (persistedItem != null) {
                    updatedItem.setUid(persistedItem.getUid());
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

    public static <T extends BaseIdentifiableObject> Map<String, T> toMap(Collection<T> objects) {
        Map<String, T> map = new HashMap<>();
        if (objects != null && objects.size() > 0) {
            for (T object : objects) {
                if (object.getUid() != null) {
                    map.put(object.getUid(), object);
                }
            }
        }
        return map;
    }
}
