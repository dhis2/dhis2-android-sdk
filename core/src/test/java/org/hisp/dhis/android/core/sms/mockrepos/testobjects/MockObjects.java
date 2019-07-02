package org.hisp.dhis.android.core.sms.mockrepos.testobjects;

import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MockObjects {
    public static String user = "AIK2aQOJIbj";
    public static String enrollmentUid = "jQK0XnMVFIK";
    public static String teiUid = "MmzaWDDruXW";
    public static String trackedEntityType = "nEenWmSyUEp";
    public static String program = "IpHINAT79UW";
    public static String orgUnit = "DiszpKrYNg8";

    public static Enrollment getTestEnrollment() {
        return Enrollment.builder()
                .uid(enrollmentUid)
                .created(new Date())
                .lastUpdated(new Date())
                .organisationUnit(orgUnit)
                .program(program)
                .enrollmentDate(new Date())
                .trackedEntityInstance(teiUid)
                .id(341L).build();
    }

    public static TrackedEntityInstance getTEIEnrollment() {
        return TrackedEntityInstance.builder()
                .uid(teiUid)
                .trackedEntityType(trackedEntityType)
                .trackedEntityAttributeValues(getTestValues())
                .enrollments(Collections.singletonList(getTestEnrollment()))
                .build();
    }

    public static ArrayList<TrackedEntityAttributeValue> getTestValues() {
        ArrayList<TrackedEntityAttributeValue> list = new ArrayList<>();
        list.add(getTestValue("w75KJ2mc4zz", "Anne"));
        list.add(getTestValue("zDhUuAYrxNC", "Anski"));
        list.add(getTestValue("cejWyOfXge6", "Female"));
        list.add(getTestValue("mLur0EGaw9A", "OU test"));
        return list;
    }

    private static TrackedEntityAttributeValue getTestValue(String attr, String value) {
        return TrackedEntityAttributeValue.builder()
                .value(value)
                .created(new Date())
                .lastUpdated(new Date())
                .trackedEntityAttribute(attr)
                .trackedEntityInstance(teiUid)
                .build();
    }
}
