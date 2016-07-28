package org.hisp.dhis.android.sdk.utils.comparators;

import org.hisp.dhis.android.sdk.persistence.models.Enrollment;

import java.util.Comparator;

/**
 * Comparator that returns the Enrollment with the smallest localId as the greater of the two given
 * Using Collections.sort will give you the biggest localId first
 */
public class EnrollmentLocalIdComparator implements Comparator<Enrollment> {

    @Override
    public int compare(Enrollment lhs, Enrollment rhs) {
        if (lhs == null && rhs == null) {
            return 0;
        } else if (lhs == null) {
            return 1;
        } else if (rhs == null) {
            return -1;
        }

        return (int) Math.ceil(rhs.getLocalId() - lhs.getLocalId());
    }
}


