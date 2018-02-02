package org.hisp.dhis.android.core.relationship;

import java.util.HashSet;
import java.util.Set;

public class RelationshipTypeQuery {

    private final Set<String> uIds;


    public RelationshipTypeQuery(Set<String> uIds) {
        this.uIds = uIds;
    }

    public Set<String> getUIds() {
        return uIds;
    }

    public static class Builder {
        private Set<String> uIds=new HashSet<>();

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder withUIds(Set<String> uIds){
            this.uIds=uIds;
            return this;
        }

        public RelationshipTypeQuery build(){
            return new RelationshipTypeQuery(uIds);
        }
    }


}
