package org.hisp.dhis.client.sdk.core.program;

import org.hisp.dhis.client.sdk.core.common.controllers.IdentifiableController;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;
import org.hisp.dhis.client.sdk.models.program.ProgramTrackedEntityAttribute;

import java.util.List;

public interface ProgramTrackedEntityAttributeController extends IdentifiableController<ProgramTrackedEntityAttribute> {
    List<DbOperation> merge(List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes) throws ApiException;
}
