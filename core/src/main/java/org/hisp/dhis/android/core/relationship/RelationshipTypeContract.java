package org.hisp.dhis.android.core.relationship;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectContract;

public class RelationshipTypeContract {
    public interface Columns extends BaseIdentifiableObjectContract.Columns {
        String B_IS_TO_A = "bIsToA";
        String A_IS_TO_B = "AIsToB";
    }
}
