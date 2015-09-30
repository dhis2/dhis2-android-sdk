package org.hisp.dhis.android.sdk.models.common.repository;

import org.hisp.dhis.android.sdk.models.common.IModel;

public interface IRepositorySave<T extends IModel> {
    void save(T object);
}
