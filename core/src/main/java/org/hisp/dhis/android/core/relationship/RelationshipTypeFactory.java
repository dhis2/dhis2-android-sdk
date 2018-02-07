package org.hisp.dhis.android.core.relationship;

import org.hisp.dhis.android.core.common.DeletableStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import retrofit2.Retrofit;

public class RelationshipTypeFactory {

    private final DatabaseAdapter databaseAdapter;
    private final RelationshipTypeService relationshipTypeService;
    private final ResourceHandler resourceHandler;
    private final RelationshipTypeHandler relationshipTypeHandler;
    private final RelationshipTypeStore relationshipTypeStore;
    private final List<DeletableStore> deletableStoreList;

    public RelationshipTypeFactory(
            Retrofit retrofit,
            DatabaseAdapter databaseAdapter,
            ResourceHandler resourceHandler) {
        this.databaseAdapter = databaseAdapter;
        this.relationshipTypeService = retrofit.create(RelationshipTypeService.class);
        this.resourceHandler = resourceHandler;
        this.relationshipTypeStore = new RelationshipTypeStoreImpl(databaseAdapter);
        this.relationshipTypeHandler = new RelationshipTypeHandler(this.relationshipTypeStore);
        this.deletableStoreList = new ArrayList<>();
        deletableStoreList.add(relationshipTypeStore);
    }

    public RelationshipTypeEndPointCall newEndPointCall(Set<String> relationshipTypeUIds,
            Date serveDate) {
        RelationshipTypeQuery relationshipTypeQuery =
                RelationshipTypeQuery.Builder.create().withUIds(relationshipTypeUIds).build();

        return new RelationshipTypeEndPointCall(relationshipTypeService, databaseAdapter,
                relationshipTypeQuery, serveDate, relationshipTypeHandler, resourceHandler);
    }

    public RelationshipTypeHandler getRelationshipTypeHandler() {
        return relationshipTypeHandler;
    }

    public RelationshipTypeStore getRelationshipTypeStore() {
        return relationshipTypeStore;
    }

    public List<DeletableStore> getDeletableStores() {
        return deletableStoreList;
    }
}
