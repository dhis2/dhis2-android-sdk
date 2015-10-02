/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.core.persistence.models.common;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.sdk.models.common.base.IModel;
import org.hisp.dhis.android.sdk.models.common.base.IStore;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.android.sdk.models.utils.Preconditions.isNull;

public abstract class AbsStore<T extends IModel> implements IStore<T>, IMappable<T> {
    private final Class<? extends Model> mClass;

    public <DatabaseEntityType extends Model & IModel> AbsStore(Class<DatabaseEntityType> clazz) {
        mClass = clazz;
    }

    @Override
    public void insert(T object) {
        isNull(object, "object must not be null");

        BaseModel databaseEntity = mapToDatabaseEntity(object);
        if (databaseEntity != null) {
            databaseEntity.insert();

            /* setting id which DbFlows' BaseModel generated after insertion */
            IModel dataBaseEntityModel = (IModel) databaseEntity;
            object.setId(dataBaseEntityModel.getId());
        }
    }

    @Override
    public void update(T object) {
        isNull(object, "object must not be null");

        BaseModel databaseEntity = mapToDatabaseEntity(object);
        if (databaseEntity != null) {
            databaseEntity.update();
        }
    }

    @Override
    public void save(T object) {
        isNull(object, "object must not be null");

        BaseModel databaseEntity = mapToDatabaseEntity(object);
        if (databaseEntity != null) {
            databaseEntity.save();

            /* setting id which DbFlows' BaseModel generated after insertion */
            IModel dataBaseEntityModel = (IModel) databaseEntity;
            object.setId(dataBaseEntityModel.getId());
        }
    }

    @Override
    public void delete(T object) {
        isNull(object, "object must not be null");

        BaseModel databaseEntity = mapToDatabaseEntity(object);
        if (databaseEntity != null) {
            databaseEntity.delete();
        }
    }

    @Override
    public List<T> queryAll() {
        List<? extends Model> databaseEntities = new Select()
                .from(mClass)
                .queryList();
        return mapToModels(databaseEntities);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <DatabaseEntityType extends Model> List<DatabaseEntityType> mapToDatabaseEntities(List<T> models) {
        List<DatabaseEntityType> modelObjects = new ArrayList<>();
        if (models != null && !models.isEmpty()) {
            for (T model : models) {
                modelObjects.add((DatabaseEntityType) mapToDatabaseEntity(model));
            }
        }
        return modelObjects;
    }

    @Override
    public <DatabaseEntityType extends Model> List<T> mapToModels(List<DatabaseEntityType> dataBaseEntities) {
        List<T> modelObjects = new ArrayList<>();
        if (dataBaseEntities != null && !dataBaseEntities.isEmpty()) {
            for (DatabaseEntityType dataBaseEntity : dataBaseEntities) {
                modelObjects.add(mapToModel(dataBaseEntity));
            }
        }
        return modelObjects;
    }

    protected final Class<? extends Model> getModelClass() {
        return mClass;
    }

    private <T extends Model & IModel> boolean isModelExists(T object) {
        //new Select().from(getModelClass()).
        return false;
    }
}
