package org.hisp.dhis.client.sdk.core.program;

import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.models.program.ProgramIndicator;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;

public interface ProgramIndicatorApiClient {
    List<ProgramIndicator> getProgramIndicators(
            Fields fields, DateTime lastUpdated, Set<String> uids) throws ApiException;

    List<ProgramIndicator> getProgramIndicators(
            Fields fields, Set<String> programIndicatorUids) throws ApiException;
}
