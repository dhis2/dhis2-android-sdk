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

package org.hisp.dhis.client.sdk.android.api.persistence.flow;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.sql.language.property.Property;
import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.android.api.persistence.DbFlowOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.models.common.base.IdentifiableObject;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.text.TextUtils.isEmpty;

@Table(database = DbDhis.class, uniqueColumnGroups = {
        @UniqueGroup(groupNumber = ModelLinkFlow.UNIQUE_LINK,
                uniqueConflict = ConflictAction.REPLACE)
})
public class ModelLinkFlow extends BaseModelFlow {
    final static int UNIQUE_LINK = 779472;
    final static String KEY_ONE = "modelKeyOne";
    final static String KEY_TWO = "modelKeyTwo";
    final static String LINK_MIME_TYPE = "linkMimeType";

    @Column(name = KEY_ONE)
    String keyOne;

    @Column(name = KEY_TWO)
    String keyTwo;

    @Column(name = LINK_MIME_TYPE)
    String linkMimeType;

    public ModelLinkFlow() {
        // explicit empty constructor
    }

    @NonNull
    public static List<DbOperation> createOperations(
            List<ModelLinkFlow> persistedLinks, List<ModelLinkFlow> updatedLinks) {

        // avoiding null checks in order to make code less verbose
        persistedLinks = persistedLinks == null ? new ArrayList<ModelLinkFlow>() : persistedLinks;
        updatedLinks = updatedLinks == null ? new ArrayList<ModelLinkFlow>() : updatedLinks;

        // Building set where each value is concatenation of first and second keys.
        // Allows to determine, which model links are already there
        Set<String> linkKeySet = new HashSet<>();
        for (ModelLinkFlow linkModel : persistedLinks) {
            linkKeySet.add(linkModel.getKeyOne() + linkModel.getKeyTwo());
        }

        List<DbOperation> dbOperations = new ArrayList<>();
        for (ModelLinkFlow linkModel : updatedLinks) {
            String key = linkModel.getKeyOne() + linkModel.getKeyTwo();
            if (!linkKeySet.contains(key)) {
                dbOperations.add(DbFlowOperation.insert(linkModel));
            }
        }

        return dbOperations;
    }

    @NonNull
    public static <T extends IdentifiableObject> Map<String, List<T>> queryLinksForModel(
            @NonNull Class<T> modelClass, @NonNull String linkMimeType) {
        List<ModelLinkFlow> persistedLinks = new Select()
                .from(ModelLinkFlow.class)
                .where(ModelLinkFlow_Table.linkMimeType.is(linkMimeType))
                .queryList();

        Map<String, List<T>> linkModels = new HashMap<>();
        for (ModelLinkFlow linkModel : persistedLinks) {
            try {
                T model = modelClass.newInstance();
                model.setUId(linkModel.getKeyTwo());

                if (linkModels.get(linkModel.getKeyOne()) == null) {
                    linkModels.put(linkModel.getKeyOne(), new ArrayList<T>());
                }

                linkModels.get(linkModel.getKeyOne()).add(model);
            } catch (IllegalAccessException illegalAccessException) {
                throw new RuntimeException(illegalAccessException);
            } catch (InstantiationException instantiationException) {
                throw new RuntimeException(instantiationException);
            }
        }

        return linkModels;
    }

    @NonNull
    public static <T extends IdentifiableObject> List<T> queryLinksForModel(
            @NonNull Class<T> modelClass, @NonNull String linkMimeType, @NonNull String uid) {

        List<ModelLinkFlow> persistedLinks = new Select()
                .from(ModelLinkFlow.class)
                .where(ModelLinkFlow_Table.linkMimeType.is(linkMimeType))
                .and(ModelLinkFlow_Table.modelKeyOne.is(uid))
                .queryList();

        List<T> models = new ArrayList<>();
        for (ModelLinkFlow linkModel : persistedLinks) {
            try {
                T model = modelClass.newInstance();
                model.setUId(linkModel.getKeyTwo());
                models.add(model);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return models;
    }

    @NonNull
    public static <T extends IdentifiableObject> List<DbOperation> updateLinksToModel(
            @NonNull T model, @Nullable List<? extends IdentifiableObject> referencedModels,
            @NonNull String linkMimeType) {

        List<ModelLinkFlow> links = new ArrayList<>();
        if (referencedModels != null) {
            for (IdentifiableObject referencedModel : referencedModels) {
                ModelLinkFlow linkModel = new ModelLinkFlow();
                linkModel.setKeyOne(model.getUId());
                linkModel.setKeyTwo(referencedModel.getUId());
                linkModel.setLinkMimeType(linkMimeType);
                links.add(linkModel);
            }
        }

        List<ModelLinkFlow> persistedLinks = new Select()
                .from(ModelLinkFlow.class)
                .where(ModelLinkFlow_Table.linkMimeType.is(linkMimeType))
                .queryList();

        return ModelLinkFlow.createOperations(persistedLinks, links);
    }

    @Nullable
    public static <T extends IdentifiableObject & Model> List<T> queryRelatedModels(
            @NonNull Class<T> modelClass, @NonNull String type,
            @NonNull List<? extends IdentifiableObject> relatedItems) {

        Set<String> uids = ModelUtils.toUidSet(relatedItems);
        System.out.println(uids);

        Property<String> uidColumn = new Property<>(modelClass,
                BaseIdentifiableObjectFlow.COLUMN_UID);
        Where<T> where = new Select()
                .from(modelClass)
                .join(ModelLinkFlow.class, Join.JoinType.LEFT_OUTER)
                .on(ModelLinkFlow_Table.modelKeyOne.eq(uidColumn))
                .where(ModelLinkFlow_Table.linkMimeType.is(type));

        if (!uids.isEmpty()) {
            where = where.and(ModelLinkFlow_Table.modelKeyTwo.in(uids));
        }

        System.out.println("SQL: " + where.toString());

        return where.queryList();
    }

    public static <T extends IdentifiableObject> void deleteRelatedModels(
            @NonNull T model, @NonNull String linkMimeType) {
        new Delete()
                .from(ModelLinkFlow.class)
                .where(ModelLinkFlow_Table.linkMimeType.is(linkMimeType))
                .and(ModelLinkFlow_Table.modelKeyOne.is(model.getUId()))
                .query();
    }

    public static <T extends IdentifiableObject> void deleteModels(@NonNull String linkMimeType) {
        new Delete()
                .from(ModelLinkFlow.class)
                .where(ModelLinkFlow_Table.linkMimeType.is(linkMimeType))
                .query();
    }

    public String getKeyOne() {
        return keyOne;
    }

    public void setKeyOne(String keyOne) {
        this.keyOne = keyOne;
    }

    public String getKeyTwo() {
        return keyTwo;
    }

    public void setKeyTwo(String keyTwo) {
        this.keyTwo = keyTwo;
    }

    public String getLinkMimeType() {
        return linkMimeType;
    }

    public void setLinkMimeType(String linkMimeType) {
        this.linkMimeType = linkMimeType;
    }

    @Override
    public void save() {
        checkKeys();
        super.save();
    }

    @Override
    public void update() {
        checkKeys();
        super.update();
    }

    @Override
    public void insert() {
        checkKeys();
        super.insert();
    }

    @Override
    public String toString() {
        return "ModelLink_Flow{" +
                "keyOne='" + keyOne + '\'' +
                ", keyTwo='" + keyTwo + '\'' +
                ", linkMimeType='" + linkMimeType + '\'' +
                '}';
    }

    private void checkKeys() {
        if (isEmpty(keyOne) || isEmpty(keyTwo)) {
            throw new IllegalArgumentException("Both keys must be present " +
                    "in model before going to database");
        }
    }
}
