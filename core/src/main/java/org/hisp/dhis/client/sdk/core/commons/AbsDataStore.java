package org.hisp.dhis.client.sdk.core.commons;

import android.content.ContentResolver;

import org.hisp.dhis.client.sdk.models.common.Model;

public class AbsDataStore<ModelType extends Model> extends AbsStore<ModelType> {

    public AbsDataStore(ContentResolver contentResolver, Mapper<ModelType> mapper) {
        super(contentResolver, mapper);
    }

    @Override
    public boolean insert(ModelType object) {
        return saveActionForModel(super.insert(object), object);
    }

    @Override
    public boolean update(ModelType object) {
        return saveActionForModel(super.update(object), object);
    }

    @Override
    public boolean save(ModelType object) {
        return saveActionForModel(super.save(object), object);
    }

    @Override
    public int delete(ModelType object) {
        return deleteActionForModel(super.delete(object), object);
    }

    @Override
    public int deleteAll() {
        return deleteAllActionsForModelType(super.deleteAll());
    }

    private boolean saveActionForModel(boolean isModelSaved, ModelType model) {
        return isModelSaved && (stateStore.queryActionForModel(model) != null ||
                stateStore.saveActionForModel(model, Action.SYNCED));
    }

    private int deleteActionForModel(boolean isDeleted, ModelType model) {
        return isDeleted && stateStore.deleteActionForModel(model);
    }

    private int deleteAllActionsForModelType(boolean areModelsRemoved) {
        return areModelsRemoved && stateStore.deleteActionsForModelType(
                getMapper().getModelTypeClass());
    }
}