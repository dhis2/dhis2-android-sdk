package org.hisp.dhis.android.sdk.models.common.repository;

import org.hisp.dhis.android.sdk.models.common.IModel;

public interface IRepositoryGet<T extends IModel> {
    T get(long id);
}
