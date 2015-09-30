package org.hisp.dhis.android.sdk.models.common.repository;

import org.hisp.dhis.android.sdk.models.common.IModel;

import java.util.List;

public interface IRepositoryList<T extends IModel> {
    List<T> list();
}
