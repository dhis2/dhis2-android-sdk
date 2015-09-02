package org.hisp.dhis.android.sdk.models.interpretation;

import org.hisp.dhis.android.sdk.models.common.IService;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;

public interface IInterpretationElementService extends IService {
    InterpretationElement createInterpretationElement(Interpretation interpretation,
                                                      DashboardElement dashboardElement,
                                                      String mimeType);
}