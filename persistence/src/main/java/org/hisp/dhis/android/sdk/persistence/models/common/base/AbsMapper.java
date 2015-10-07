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

package org.hisp.dhis.android.sdk.persistence.models.common.base;

import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.sdk.models.common.base.IModel;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsMapper<ModelType extends IModel, DatabaseEntityType extends IModel & Model> implements IMapper<ModelType, DatabaseEntityType> {

    @Override
    public List<DatabaseEntityType> mapToDatabaseEntities(List<ModelType> models) {
        List<DatabaseEntityType> modelObjects = new ArrayList<>();
        if (models != null && !models.isEmpty()) {
            for (ModelType model : models) {
                modelObjects.add(mapToDatabaseEntity(model));
            }
        }
        return modelObjects;
    }

    @Override
    public List<ModelType> mapToModels(List<DatabaseEntityType> dataBaseEntities) {
        List<ModelType> modelObjects = new ArrayList<>();
        if (dataBaseEntities != null && !dataBaseEntities.isEmpty()) {
            for (DatabaseEntityType dataBaseEntity : dataBaseEntities) {
                modelObjects.add(mapToModel(dataBaseEntity));
            }
        }
        return modelObjects;
    }
}
