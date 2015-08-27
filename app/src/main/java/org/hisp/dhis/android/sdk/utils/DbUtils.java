package org.hisp.dhis.android.sdk.utils;

import com.raizlabs.android.dbflow.runtime.TransactionManager;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.BaseIdentifiableObject;
import org.hisp.dhis.android.sdk.persistence.models.meta.DbOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.android.sdk.persistence.models.BaseIdentifiableObject.toMap;
import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

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
    public static void applyBatch(final Collection<DbOperation> operations) {
        isNull(operations, "List<DbOperation> object must not be null");

        if (operations.isEmpty()) {
            return;
        }

        TransactionManager.transact(Dhis2Database.NAME, new Runnable() {
            @Override
            public void run() {
                for (DbOperation operation : operations) {
                    switch (operation.getAction()) {
                        case INSERT: {
                            operation.getModel().insert();
                            break;
                        }
                        case UPDATE: {
                            operation.getModel().update();
                            break;
                        }
                        case SAVE:
                            operation.getModel().save();
                            break;
                        case DELETE: {
                            operation.getModel().delete();
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * This utility method allows to determine which type of operation to apply to
     * each BaseIdentifiableObject depending on TimeStamp.
     *
     * @param oldModels List of models from local storage.
     * @param newModels List of models of distance instance of DHIS.
     */
    public static <T extends BaseIdentifiableObject> List<DbOperation> createOperations(List<T> oldModels,
                                                                                        List<T> newModels,
                                                                                        boolean keepOldValues) {
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
                if(!keepOldValues) {
                    ops.add(DbOperation.delete(oldModel));
                }

                // in case if there is no new model object,
                // we can jump to next iteration.
                continue;
            }

            // if the last updated field in up to date model is after the same
            // field in persisted model, it means we need to update it.
            if (oldModel.getLastUpdated() == null || newModel.getLastUpdated() == null ||
                    newModel.getLastUpdated().isAfter(oldModel.getLastUpdated())) {
                // note, we need to pass database primary id to updated model
                // in order to avoid creation of new object.
                newModel.setUid(oldModel.getUid());
                ops.add(DbOperation.update(newModel));
            }

            // as we have processed given old (persisted) model,
            // we can remove it from map of new models.
            newModelsMap.remove(oldModelKey);
        }

        // Inserting new items.
        for (String newModelKey : newModelsMap.keySet()) {
            T item = newModelsMap.get(newModelKey);
            ops.add(DbOperation.save(item));
        }

        return ops;
    }
}