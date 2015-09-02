package org.hisp.dhis.android.sdk.models.interpretation;

import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;

public final class InterpretationElementService implements IInterpretationElementService {

    public InterpretationElementService() {
        // empty constructor
    }

    /**
     * Factory method which allows to create InterpretationElement
     * by using DashboardElement as main source of data.
     *
     * @param interpretation   Interpretation to which we will assign interpretation element
     * @param dashboardElement DashboardElement from which we want to create interpretation element.
     * @return new InterpretationElement
     */
    @Override
    public InterpretationElement createInterpretationElement(Interpretation interpretation,
                                                             DashboardElement dashboardElement,
                                                             String mimeType) {
        InterpretationElement interpretationElement = new InterpretationElement();
        interpretationElement.setUId(dashboardElement.getUId());
        interpretationElement.setName(dashboardElement.getName());
        interpretationElement.setDisplayName(dashboardElement.getDisplayName());
        interpretationElement.setCreated(dashboardElement.getCreated());
        interpretationElement.setLastUpdated(dashboardElement.getLastUpdated());
        interpretationElement.setAccess(dashboardElement.getAccess());
        interpretationElement.setType(mimeType);
        interpretationElement.setInterpretation(interpretation);
        return interpretationElement;
    }
}