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

package org.hisp.dhis.android.sdk.corejava.common.persistence;

import org.hisp.dhis.android.sdk.models.common.base.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.common.base.IdentifiableObject;
import org.hisp.dhis.android.sdk.models.common.meta.DbOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.android.sdk.models.common.base.BaseIdentifiableObject.toMap;

/**
 * This class is intended to process list of DbOperations
 * during single database transaction
 */
public final class DbUtils {
    private DbUtils() {
        // no instances
    }

    /**
     * Performs each given DbOperation during one database transaction
     *
     * @param operations List of DbOperations to be performed.
     */
    /* public void applyBatch(final Collection<IDbOperation> operations) {
        isNull(operations, "List<DbOperation> object must not be null");

        if (operations.isEmpty()) {
            return;
        }

        transactionManager.transact(new Runnable() {
            @Override
            public void run() {
                for (IDbOperation operation : operations) {
                    operation.execute();
                }
            }
        });
    } */

    /**
     * This utility method allows to determine which type of operation to apply to
     * each BaseIdentifiableObject$Flow depending on TimeStamp.
     *
     * @param oldModels List of models from local storage.
     * @param newModels List of models of distance instance of DHIS.
     */
    public static <T extends IdentifiableObject> List<DbOperation> createOperations(IIdentifiableObjectStore<T> modelStore,
                                                                                    List<T> oldModels, List<T> newModels) {
        List<DbOperation> ops = new ArrayList<>();

        Map<String, T> newModelsMap = toMap(newModels);
        Map<String, T> oldModelsMap = toMap(oldModels);

        // As we will go through map of persisted items, we will try to update existing data.
        // Also, during each iteration we will remove old model key from list of new models.
        // As the result, the list of remaining items in newModelsMap,
        // will contain only those items which were not inserted before.
        for (String oldModelKey : oldModelsMap.keySet()) {
            T newModel = newModelsMap.get(oldModelKey);
            T oldModel = oldModelsMap.get(oldModelKey);

            // if there is no particular model with given uid in list of
            // actual (up to date) items, it means it was removed on the server side
            if (newModel == null) {
                ops.add(DbOperation.with(modelStore)
                        .delete(oldModel));

                // in case if there is no new model object,
                // we can jump to next iteration.
                continue;
            }

            // if the last updated field in up to date model is after the same
            // field in persisted model, it means we need to update it.
            if (newModel.getLastUpdated().isAfter(oldModel.getLastUpdated())) {
                // note, we need to pass database primary id to updated model
                // in order to avoid creation of new object.
                newModel.setId(oldModel.getId());
                ops.add(DbOperation.with(modelStore)
                        .update(newModel));
            }

            // as we have processed given old (persisted) model,
            // we can remove it from map of new models.
            newModelsMap.remove(oldModelKey);
        }

        // Inserting new items.
        for (String newModelKey : newModelsMap.keySet()) {
            T item = newModelsMap.get(newModelKey);
            ops.add(DbOperation.with(modelStore)
                    .insert(item));
        }

        return ops;
    }
}