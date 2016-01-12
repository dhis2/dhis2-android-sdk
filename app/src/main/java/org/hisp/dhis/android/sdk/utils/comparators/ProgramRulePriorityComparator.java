package org.hisp.dhis.android.sdk.utils.comparators;

import org.hisp.dhis.android.sdk.persistence.models.ProgramRule;

import java.util.Comparator;

public class ProgramRulePriorityComparator implements Comparator<ProgramRule> {
    @Override
    public int compare(ProgramRule lhs, ProgramRule rhs) {
        if(lhs == null && rhs == null) {
            return 0;
        } else if(lhs == null) {
            return 1;
        } else if(rhs == null){
            return -1;
        }
        if(lhs.getPriority() == null && rhs.getPriority() == null) {
            return 0;
        } else if(lhs.getPriority() == null) {
            return 1;
        } else if(rhs.getPriority() == null){
            return -1;
        }
        if(lhs.getPriority() < rhs.getPriority()) {
            return -1;
        } else if(lhs.getPriority().equals(rhs.getPriority())) {
            return 0;
        } else {
            return 1;
        }
    }
}
