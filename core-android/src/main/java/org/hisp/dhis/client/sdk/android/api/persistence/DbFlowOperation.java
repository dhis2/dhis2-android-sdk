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

package org.hisp.dhis.client.sdk.android.api.persistence;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.client.sdk.core.common.persistence.DbAction;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;


/**
 * This class is intended to implement partial
 * functionality of ContentProviderOperation for DbFlow.
 */
public final class DbFlowOperation<T extends BaseModel> implements IDbOperation<T> {
    private final DbAction mDbAction;
    private final T mModel;

    private DbFlowOperation(DbAction dbAction, T model) {
        mModel = isNull(model, "IdentifiableObject object must nto be null,");
        mDbAction = isNull(dbAction, "BaseModel.DbAction object must not be null");
    }

    public T getModel() {
        return mModel;
    }

    public DbAction getAction() {
        return mDbAction;
    }

    public void execute() {
        switch (getAction()) {
            case INSERT: {
                mModel.insert();
                break;
            }
            case UPDATE: {
                mModel.update();
                break;
            }
            case SAVE: {
                mModel.save();
                break;
            }
            case DELETE: {
                mModel.delete();
                break;
            }
        }
    }

    public static <Type extends BaseModel> DbFlowOperation<Type> insert(Type model) {
        return new DbFlowOperation<>(DbAction.INSERT, model);
    }

    public static <Type extends BaseModel> DbFlowOperation<Type> update(Type model) {
        return new DbFlowOperation<>(DbAction.UPDATE, model);
    }

    public static <Type extends BaseModel> DbFlowOperation<Type> save(Type model) {
        return new DbFlowOperation<>(DbAction.SAVE, model);
    }

    public static <Type extends BaseModel> DbFlowOperation<Type> delete(Type model) {
        return new DbFlowOperation<>(DbAction.DELETE, model);
    }
}
