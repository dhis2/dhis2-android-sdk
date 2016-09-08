package org.hisp.dhis.client.sdk.core.program;

import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.models.program.ProgramTrackedEntityAttribute;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;

public interface ProgramTrackedEntityAttributeApiClient {
    List<ProgramTrackedEntityAttribute> getProgramTrackedEntityAttributes(
            Fields fields, DateTime lastUpdated, Set<String> uids) throws ApiException;

    List<ProgramTrackedEntityAttribute> getProgramTrackedEntityAttributes(
            Fields fields, Set<String> programTrackedEntityAttributeUids) throws ApiException;
}
