package org.hisp.dhis.client.sdk.core.optionset;

import org.hisp.dhis.client.sdk.core.common.controllers.IdentifiableController;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;

import java.util.List;

public interface OptionSetController extends IdentifiableController<OptionSet> {
    List<DbOperation> merge(List<OptionSet> optionSets) throws ApiException;
}
