package org.hisp.dhis.android.sdk.ui.dialogs;

import org.hisp.dhis.android.sdk.persistence.models.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;

import java.util.Comparator;
import java.util.Map;

class TrackedEntityAttributeValueByIndexInProgramSorter implements Comparator<TrackedEntityAttributeValue> {

    private final Map<String, ProgramTrackedEntityAttribute> programTrackedEntityAttributeMap;

    TrackedEntityAttributeValueByIndexInProgramSorter(Map<String, ProgramTrackedEntityAttribute> programTrackedEntityAttributeMap) {
        this.programTrackedEntityAttributeMap = programTrackedEntityAttributeMap;
    }

    @Override
    public int compare(TrackedEntityAttributeValue lhs, TrackedEntityAttributeValue rhs) {
        if(programTrackedEntityAttributeMap == null) {
            return 0;
        }
        ProgramTrackedEntityAttribute lhsProgramTrackedEntityAttribute = programTrackedEntityAttributeMap.get(lhs.getTrackedEntityAttributeId());
        ProgramTrackedEntityAttribute rhsProgramTrackedEntityAttribute = programTrackedEntityAttributeMap.get(rhs.getTrackedEntityAttributeId());
        if(lhsProgramTrackedEntityAttribute == null && rhsProgramTrackedEntityAttribute == null) {
            return 0;
        } else if(lhsProgramTrackedEntityAttribute == null) {
            return -1;
        } else if(rhsProgramTrackedEntityAttribute == null) {
            return 1;
        }

        if(lhsProgramTrackedEntityAttribute.getSortOrder() > rhsProgramTrackedEntityAttribute.getSortOrder()) {
            return 1;
        } else if(rhsProgramTrackedEntityAttribute.getSortOrder() > lhsProgramTrackedEntityAttribute.getSortOrder()) {
            return -1;
        }

        return 0;
    }
}
