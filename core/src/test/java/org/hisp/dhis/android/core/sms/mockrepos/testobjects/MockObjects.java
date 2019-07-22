package org.hisp.dhis.android.core.sms.mockrepos.testobjects;

import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipItem;
import org.hisp.dhis.android.core.relationship.RelationshipItemTrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MockObjects {
    public static String user = "AIK2aQOJIbj";
    public static String enrollmentUid = "jQK0XnMVFIK";
    public static String teiUid = "MmzaWDDruXW";
    public static String teiUid2 = "ggg3R9nRSTI";
    public static String trackedEntityType = "nEenWmSyUEp";
    public static String program = "IpHINAT79UW";
    public static String orgUnit = "DiszpKrYNg8";
    public static String attributeOptionCombo = "w5hsiyYZfuR";
    public static String categoryOptionCombo = "HllvX50cXC0";
    public static String eventUid = "gqmgkrLT3XH";
    public static String programStage = "bUzhUa4QWbQ";
    public static String period = "2019";
    public static String relationship = "Tj1ddhpeCFL";
    public static String relationshipType = "R74HPJyNLs9";
    public static String dataSetUid = "R75HPJyNLs2";

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
                .trackedEntityAttributeValues(getTestAttributeValues())
                .enrollments(Collections.singletonList(getTestEnrollment()))
                .build();
    }

    public static ArrayList<TrackedEntityAttributeValue> getTestAttributeValues() {
        ArrayList<TrackedEntityAttributeValue> list = new ArrayList<>();
        for (String[] val : getValues()) {
            list.add(getTestAttributeValue(val[0], val[1]));
        }
        return list;
    }

    private static TrackedEntityAttributeValue getTestAttributeValue(String attr, String value) {
        return TrackedEntityAttributeValue.builder()
                .value(value)
                .created(new Date())
                .lastUpdated(new Date())
                .trackedEntityAttribute(attr)
                .trackedEntityInstance(teiUid)
                .build();
    }

    public static Event getSimpleEvent() {
        return Event.builder()
                .attributeOptionCombo(attributeOptionCombo)
                .program(program)
                .uid(eventUid)
                .lastUpdated(new Date())
                .trackedEntityDataValues(getTeiDataValues())
                .organisationUnit(orgUnit)
                .status(EventStatus.COMPLETED)
                .build();
    }

    public static Event getTrackerEvent() {
        return Event.builder()
                .attributeOptionCombo(attributeOptionCombo)
                .uid(eventUid)
                .lastUpdated(new Date())
                .trackedEntityDataValues(getTeiDataValues())
                .organisationUnit(orgUnit)
                .enrollment(enrollmentUid)
                .programStage(programStage)
                .status(EventStatus.COMPLETED)
                .build();
    }

    public static ArrayList<TrackedEntityDataValue> getTeiDataValues() {
        ArrayList<TrackedEntityDataValue> list = new ArrayList<>();
        for (String[] val : getValues()) {
            list.add(getTeiDataValue(val[0], val[1]));
        }
        return list;
    }

    private static TrackedEntityDataValue getTeiDataValue(String dataElement, String value) {
        return TrackedEntityDataValue.builder()
                .value(value)
                .created(new Date())
                .lastUpdated(new Date())
                .dataElement(dataElement)
                .event(eventUid)
                .build();
    }

    public static ArrayList<DataValue> getDataValues() {
        ArrayList<DataValue> list = new ArrayList<>();
        for (String[] val : getValues()) {
            list.add(getDataValue(val[0], val[1]));
        }
        return list;
    }

    private static DataValue getDataValue(String dataElement, String value) {
        return DataValue.builder()
                .attributeOptionCombo(attributeOptionCombo)
                .categoryOptionCombo(categoryOptionCombo)
                .dataElement(dataElement)
                .value(value)
                .organisationUnit(orgUnit)
                .period(period)
                .build();
    }

    private static String[][] getValues() {
        return new String[][]{
                {"UXz7xuGCEhU", "2"},
                {"X8zyunlgUfM", "Replacement"},
                {"a3kGcGDCuk6", "2019"},
                {"bx6fsa0t90x", "true"}
        };
    }

    public static Relationship getRelationship() {
        RelationshipItem from = RelationshipItem.builder()
                .trackedEntityInstance(
                        RelationshipItemTrackedEntityInstance.builder()
                                .trackedEntityInstance(teiUid)
                                .build()
                ).build();
        RelationshipItem to = RelationshipItem.builder()
                .trackedEntityInstance(
                        RelationshipItemTrackedEntityInstance.builder()
                                .trackedEntityInstance(teiUid2)
                                .build()
                ).build();
        return Relationship.builder()
                .from(from)
                .to(to)
                .relationshipType(relationshipType)
                .uid(relationship)
                .build();
    }
}
