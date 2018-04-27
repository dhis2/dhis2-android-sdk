package org.hisp.dhis.android.core.trackedentity;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

@AutoValue
public abstract class TrackedEntityAttributeReservedValueQuery extends BaseQuery {
    public abstract String trackedEntityAttributeUid();

    public abstract Integer numberToReserve();

    public abstract OrganisationUnit organisationUnit();

    public static TrackedEntityAttributeReservedValueQuery create(String trackedEntityAttributeUid,
                                                                  Integer numberToReserve,
                                                                  OrganisationUnit organisationUnit) {
        return new AutoValue_TrackedEntityAttributeReservedValueQuery(1, BaseQuery.DEFAULT_PAGE_SIZE, false,
                trackedEntityAttributeUid, numberToReserve, organisationUnit);
    }
}