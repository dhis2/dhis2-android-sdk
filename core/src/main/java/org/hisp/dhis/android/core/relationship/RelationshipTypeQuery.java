package org.hisp.dhis.android.core.relationship;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;
import java.util.Set;

@AutoValue
public abstract class RelationshipTypeQuery extends BaseQuery{

    public abstract Set<String> uIds();

    public static RelationshipTypeQuery.Builder builder() {
        return new AutoValue_RelationshipTypeQuery.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseQuery.Builder<RelationshipTypeQuery.Builder> {

        public abstract Builder uIds(Set<String> uIds);

        public abstract RelationshipTypeQuery build();
    }

}
