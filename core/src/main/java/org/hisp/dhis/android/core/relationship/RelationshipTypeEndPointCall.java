package org.hisp.dhis.android.core.relationship;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class RelationshipTypeEndPointCall implements Call<Response<Payload<RelationshipType>>> {
    private final RelationshipTypeService relationshipTypeService;
    private final DatabaseAdapter databaseAdapter;
    private final RelationshipTypeQuery relationshipTypeQuery;
    private final Date serverDate;
    private final RelationshipTypeHandler relationshipTypeHandler;
    private final ResourceHandler resourceHandler;

    private boolean isExecuted;

    public RelationshipTypeEndPointCall(
            RelationshipTypeService relationshipTypeService,
            DatabaseAdapter databaseAdapter,
            RelationshipTypeQuery relationshipTypeQuery, Date serverDate,
            RelationshipTypeHandler relationshipTypeHandler,
            ResourceHandler resourceHandler) {
        this.relationshipTypeService = relationshipTypeService;
        this.databaseAdapter = databaseAdapter;
        this.relationshipTypeQuery = relationshipTypeQuery;
        this.serverDate = new Date(serverDate.getTime());
        this.relationshipTypeHandler = relationshipTypeHandler;
        this.resourceHandler = resourceHandler;

        if (relationshipTypeQuery.getUIds() != null
                && relationshipTypeQuery.getUIds().size() > MAX_UIDS) {
            throw new IllegalArgumentException(
                    "Can't handle the amount of relationshipType: "
                            + relationshipTypeQuery.getUIds().size() + ". " +
                            "Max size is: " + MAX_UIDS);
        }
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<Payload<RelationshipType>> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }
            isExecuted = true;
        }

        String lastSyncedRelationshipType = resourceHandler.getLastUpdated(
                ResourceModel.Type.RELATIONSHIP_TYPE);

        Response<Payload<RelationshipType>> relationshipTypeByUId =
                relationshipTypeService.getRelationshipTypes(getFields(),
                        RelationshipType.uid.in(relationshipTypeQuery.getUIds()),
                        RelationshipType.lastUpdated.gt(lastSyncedRelationshipType)).execute();

        if (relationshipTypeByUId.isSuccessful() && relationshipTypeByUId.body().items() != null) {

            Transaction transaction = databaseAdapter.beginNewTransaction();

            try {
                List<RelationshipType> relationshipTypes = relationshipTypeByUId.body().items();

                for (RelationshipType relationshipType : relationshipTypes) {
                    relationshipTypeHandler.handleRelationshipType(relationshipType);
                }
                resourceHandler.handleResource(ResourceModel.Type.RELATIONSHIP_TYPE, serverDate);
                transaction.setSuccessful();
            } finally {
                transaction.end();
            }
        }
        return relationshipTypeByUId;
    }

    private Fields<RelationshipType> getFields() {
        return Fields.<RelationshipType>builder().fields(RelationshipType.uid,
                RelationshipType.code,
                RelationshipType.name, RelationshipType.displayName, RelationshipType.created,
                RelationshipType.lastUpdated, RelationshipType.deleted, RelationshipType.bIsToA,
                RelationshipType.aIsToB).build();
    }
}
