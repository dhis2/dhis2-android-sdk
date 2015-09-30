package org.hisp.dhis.android.sdk.models.common.repository;

import org.hisp.dhis.android.sdk.models.common.IdentifiableObject;

public interface IRepositoryIdentifiable<T extends IdentifiableObject> {
    T get(String uid);
}
