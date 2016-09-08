package org.hisp.dhis.client.sdk.core.trackedentity;

import org.hisp.dhis.client.sdk.core.common.controllers.IdentifiableController;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

import java.util.List;

public interface TrackedEntityController extends IdentifiableController<TrackedEntity> {
    List<DbOperation> merge(List<TrackedEntity> trackedEntities) throws ApiException;
}
