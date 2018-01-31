package org.hisp.dhis.android.core.relationship;

import static org.hisp.dhis.android.core.calls.Call.MAX_UIDS;

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

        public Builder withUIds(Set<String> uIds){
            this.uIds=uIds;
            return this;
        }

        public RelationshipTypeQuery build(){
            if (uIds != null && uIds.size() > MAX_UIDS) {
                throw new IllegalArgumentException(
                        "Can't handle the amount of relationShipType: "
                                + uIds.size() + ". " +
                                "Max size is: " + MAX_UIDS);
            }
            return new RelationshipTypeQuery(uIds);
        }
    }


}
