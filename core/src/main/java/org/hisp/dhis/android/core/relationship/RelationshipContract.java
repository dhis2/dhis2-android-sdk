package org.hisp.dhis.android.core.relationship;

import org.hisp.dhis.android.core.common.BaseModelContract;

public class RelationshipContract {

    public interface Columns extends BaseModelContract.Columns {

        String TRACKED_ENTITY_INSTANCE_A = "trackedEntityInstanceA";
        String TRACKED_ENTITY_INSTANCE_B = "trackedEntityInstanceB";
        String RELATIONSHIP_TYPE = "relationshipType";
    }
}
