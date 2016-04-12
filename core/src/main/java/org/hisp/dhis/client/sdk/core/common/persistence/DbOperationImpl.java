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

import org.hisp.dhis.client.sdk.models.common.base.Model;
import org.hisp.dhis.client.sdk.utils.Preconditions;

/**
 * This class is intended to implement partial
 * functionality of ContentProviderOperation for DbFlow.
 */
public final class DbOperationImpl<T extends Model> implements DbOperation<T> {
    private final DbAction mDbAction;
    private final T mModel;
    private final Store<T> mModelStore;

    private DbOperationImpl(DbAction dbAction, T model, Store<T> store) {
        mModel = Preconditions.isNull(model, "IdentifiableObject object must not be null,");
        mDbAction = Preconditions.isNull(dbAction, "BaseModel.DbAction object must not be null");
        mModelStore = Preconditions.isNull(store, "Store object must not be null");
    }

    public static <T extends Model> DbOperationBuilder<T> with(Store<T> store) {
        return new DbOperationBuilder<>(store);
    }

    @Override
    public T getModel() {
        return mModel;
    }

    @Override
    public DbAction getAction() {
        return mDbAction;
    }

    @Override
    public void execute() {
        switch (mDbAction) {
            case INSERT: {
                mModelStore.insert(mModel);
                break;
            }
            case UPDATE: {
                mModelStore.update(mModel);
                break;
            }
            case SAVE: {
                mModelStore.save(mModel);
                break;
            }
            case DELETE: {
                mModelStore.delete(mModel);
                break;
            }
        }
    }

    public Store<T> getStore() {
        return mModelStore;
    }

    public static class DbOperationBuilder<T extends Model> {
        private final Store<T> mStore;

        DbOperationBuilder(Store<T> store) {
            mStore = store;
        }

        public DbOperationImpl<T> insert(T model) {
            return new DbOperationImpl<>(DbAction.INSERT, model, mStore);
        }

        public DbOperationImpl<T> update(T model) {
            return new DbOperationImpl<>(DbAction.UPDATE, model, mStore);
        }

        public DbOperationImpl<T> save(T model) {
            return new DbOperationImpl<>(DbAction.SAVE, model, mStore);
        }

        public DbOperationImpl<T> delete(T model) {
            return new DbOperationImpl<>(DbAction.DELETE, model, mStore);
        }
    }
}
