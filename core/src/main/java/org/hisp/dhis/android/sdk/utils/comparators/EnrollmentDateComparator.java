package org.hisp.dhis.android.sdk.utils.comparators;

import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.joda.time.DateTime;

import java.util.Comparator;

import static android.text.TextUtils.isEmpty;

/**
 * Comparator that returns the Enrollment with the EARLIEST enrollmentDate as the greater of the two given.
 */
public class EnrollmentDateComparator implements Comparator<Enrollment> {
    @Override
    public int compare(Enrollment lhs, Enrollment rhs) {
        if(lhs == null && rhs == null) {
            return 0;
        } else if (lhs == null) {
            return 1;
        } else if (rhs == null) {
            return -1;
        }
        if(isEmpty(lhs.getEnrollmentDate()) && isEmpty(rhs.getEnrollmentDate())) {
            return 0;
        } else if (isEmpty(lhs.getEnrollmentDate())) {
            return 1;
        } else if (isEmpty(rhs.getEnrollmentDate())) {
            return -1;
        }
        DateTime lhsDate = new DateTime(lhs.getEnrollmentDate());
        DateTime rhsDate = new DateTime(rhs.getEnrollmentDate());
        if(lhsDate == null && rhsDate == null) {
            return 0;
        } else if (lhsDate == null) {
            return 1;
        } else if (rhsDate == null) {
            return -1;
        } else {
            if(lhsDate.isBefore(rhsDate)) {
                return 1;
            } else if(lhsDate.isAfter(rhsDate)) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
