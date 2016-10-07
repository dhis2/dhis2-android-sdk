package org.hisp.dhis.client.sdk.core.commons;

import org.hisp.dhis.client.sdk.models.common.IdentifiableObject;

public interface IdentifiableObjectDataStore<T extends IdentifiableObject> {
    T queryByUid(String uid);

    T queryByCode(String code);

}
