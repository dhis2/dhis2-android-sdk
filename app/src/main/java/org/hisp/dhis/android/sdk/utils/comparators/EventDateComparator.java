package org.hisp.dhis.android.sdk.utils.comparators;

import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.joda.time.DateTime;

import java.util.Comparator;

import static android.text.TextUtils.isEmpty;

/**
 * Comparator that returns the Event with the latest EventDate as the greater of the two given.
 */
public class EventDateComparator implements Comparator<Event> {
    @Override
    public int compare(Event lhs, Event rhs) {
        if(lhs == null && rhs == null) {
            return 0;
        } else if (lhs == null) {
            return -1;
        } else if (rhs == null) {
            return 1;
        }
        if(isEmpty(lhs.getEventDate()) && isEmpty(rhs.getEventDate())) {
            return 0;
        } else if (isEmpty(lhs.getEventDate())) {
            return -1;
        } else if (isEmpty(rhs.getEventDate())) {
            return 1;
        }
        DateTime lhsDate = new DateTime(lhs.getEventDate());
        DateTime rhsDate = new DateTime(rhs.getEventDate());
        if(lhsDate == null && rhsDate == null) {
            return 0;
        } else if (lhsDate == null) {
            return -1;
        } else if (rhsDate == null) {
            return 1;
        } else {
            if(lhsDate.isBefore(rhsDate)) {
                return -1;
            } else if(lhsDate.isAfter(rhsDate)) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
