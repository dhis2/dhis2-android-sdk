package org.hisp.dhis.android.core.relationship;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;
import java.util.Set;

@AutoValue
public abstract class RelationshipTypeQuery extends BaseQuery{

    public abstract Set<String> uids();

    public static RelationshipTypeQuery.Builder builder() {
        return new AutoValue_RelationshipTypeQuery.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseQuery.Builder<RelationshipTypeQuery.Builder> {

        public abstract Builder uids(Set<String> uids);

        public abstract RelationshipTypeQuery build();
    }

}
