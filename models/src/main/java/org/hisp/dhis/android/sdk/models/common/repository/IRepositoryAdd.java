package org.hisp.dhis.android.sdk.models.common.repository;

import org.hisp.dhis.android.sdk.models.common.IModel;

public interface IRepositoryAdd<T extends IModel> {
    void add();
}
