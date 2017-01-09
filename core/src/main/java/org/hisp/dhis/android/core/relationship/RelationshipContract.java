package org.hisp.dhis.android.core.relationship;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectContract;

public class RelationshipContract {

    public interface Columns {
        String ID = BaseIdentifiableObjectContract.Columns.ID;
        String TRACKED_ENTITY_INSTANCE_A = "trackedEntityInstanceA";
        String TRACKED_ENTITY_INSTANCE_B = "trackedEntityInstanceB";
        String RELATIONSHIP_TYPE = "relationshipType";
    }
}
