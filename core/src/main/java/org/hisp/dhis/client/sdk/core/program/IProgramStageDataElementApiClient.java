package org.hisp.dhis.client.sdk.core.program;

import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.joda.time.DateTime;

import java.util.List;

public interface IProgramStageDataElementApiClient {
    List<ProgramStageDataElement> getProgramStageDataElements(Fields fields, DateTime lastUpdated,
                                                      String... uids) throws ApiException;
}
