package org.hisp.dhis.android.sdk.models.common.repository;

import org.hisp.dhis.android.sdk.models.common.IModel;

public interface IRepositoryRemove<T extends IModel> {
    void remove(T object);
}
