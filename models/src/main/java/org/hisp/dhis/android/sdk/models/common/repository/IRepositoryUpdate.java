package org.hisp.dhis.android.sdk.models.common.repository;

import org.hisp.dhis.android.sdk.models.common.IModel;

public interface IRepositoryUpdate<T extends IModel> {
    void update(T object);
}
