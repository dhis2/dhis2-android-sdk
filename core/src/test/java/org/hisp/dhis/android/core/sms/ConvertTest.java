
package org.hisp.dhis.android.core.sms;

import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.sms.domain.interactor.QrCodeCase;
import org.hisp.dhis.android.core.sms.mockrepos.MockLocalDbRepository;
import org.hisp.dhis.android.core.sms.mockrepos.testobjects.MockMetadata;
import org.hisp.dhis.android.core.sms.mockrepos.testobjects.MockObjects;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.smscompression.SMSConsts;
import org.hisp.dhis.smscompression.SMSSubmissionReader;
import org.hisp.dhis.smscompression.models.AggregateDatasetSMSSubmission;
import org.hisp.dhis.smscompression.models.DeleteSMSSubmission;
import org.hisp.dhis.smscompression.models.EnrollmentSMSSubmission;
import org.hisp.dhis.smscompression.models.RelationshipSMSSubmission;
import org.hisp.dhis.smscompression.models.SMSAttributeValue;
import org.hisp.dhis.smscompression.models.SMSDataValue;
import org.hisp.dhis.smscompression.models.SMSSubmission;
import org.hisp.dhis.smscompression.models.SimpleEventSMSSubmission;
import org.hisp.dhis.smscompression.models.TrackerEventSMSSubmission;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ConvertTest {
    private MockLocalDbRepository testLocalDb;

    @Before
    public void init() {
        testLocalDb = new MockLocalDbRepository();
    }

    @Test
    public void convertEnrollment() throws Exception {
        EnrollmentSMSSubmission s = (EnrollmentSMSSubmission) convert(new QrCodeCase(testLocalDb)
                .generateEnrollmentCode(MockObjects.enrollmentUid));
        assertEquals(s.getUserID().uid, MockObjects.user);
        assertEquals(s.getEnrollment().uid, MockObjects.enrollmentUid);
        assertEquals(s.getTrackedEntityInstance().uid, MockObjects.teiUid);
        assertEquals(s.getTrackedEntityType().uid, MockObjects.trackedEntityType);
        assertEquals(s.getOrgUnit().uid, MockObjects.orgUnit);
        assertEquals(s.getTrackerProgram().uid, MockObjects.program);
        for (SMSAttributeValue item : s.getValues()) {
            assertTrue(containsAttributeValue(MockObjects.getTestAttributeValues(), item));
        }
    }

    @Test
    public void convertSimpleEvent() throws Exception {
        SimpleEventSMSSubmission s = (SimpleEventSMSSubmission) convert(new QrCodeCase(testLocalDb)
                .generateSimpleEventCode(MockObjects.eventUid));
        assertEquals(s.getUserID().uid, MockObjects.user);
        assertEquals(s.getOrgUnit().uid, MockObjects.orgUnit);
        assertEquals(s.getEventProgram().uid, MockObjects.program);
        assertEquals(s.getAttributeOptionCombo().uid, MockObjects.attributeOptionCombo);
        assertEquals(s.getEvent().uid, MockObjects.eventUid);
        assertEquals(s.getEventStatus(), SMSConsts.SMSEventStatus.COMPLETED);
        for (SMSDataValue item : s.getValues()) {
            assertTrue(containsTeiDataValue(MockObjects.getTeiDataValues(), item));
        }
    }

    @Test
    public void convertTrackerEvent() throws Exception {
        TrackerEventSMSSubmission s = (TrackerEventSMSSubmission) convert(new QrCodeCase(testLocalDb)
                .generateTrackerEventCode(MockObjects.eventUid));
        assertEquals(s.getUserID().uid, MockObjects.user);
        assertEquals(s.getOrgUnit().uid, MockObjects.orgUnit);
        assertEquals(s.getAttributeOptionCombo().uid, MockObjects.attributeOptionCombo);
        assertEquals(s.getEvent().uid, MockObjects.eventUid);
        assertEquals(s.getProgramStage().uid, MockObjects.programStage);
        assertEquals(s.getEnrollment().uid, MockObjects.enrollmentUid);
        assertEquals(s.getEventStatus(), SMSConsts.SMSEventStatus.COMPLETED);
        for (SMSDataValue item : s.getValues()) {
            assertTrue(containsTeiDataValue(MockObjects.getTeiDataValues(), item));
        }
    }

    @Test
    public void convertDataSet() throws Exception {
        AggregateDatasetSMSSubmission s = (AggregateDatasetSMSSubmission) convert(new QrCodeCase(testLocalDb)
                .generateDataSetCode(MockObjects.dataSetUid, MockObjects.orgUnit,
                        MockObjects.period, MockObjects.attributeOptionCombo));
        assertEquals(s.getUserID().uid, MockObjects.user);
        assertEquals(s.getOrgUnit().uid, MockObjects.orgUnit);
        assertEquals(s.getAttributeOptionCombo().uid, MockObjects.attributeOptionCombo);
        assertEquals(s.getDataSet().uid, MockObjects.dataSetUid);
        assertEquals(s.getPeriod(), MockObjects.period);
        for (SMSDataValue item : s.getValues()) {
            assertTrue(containsDataValue(MockObjects.getDataValues(), item));
        }
    }

    @Test
    public void convertRelationship() throws Exception {
        RelationshipSMSSubmission s = (RelationshipSMSSubmission) convert(new QrCodeCase(testLocalDb)
                .generateRelationshipCode(MockObjects.relationship));
        assertEquals(s.getUserID().uid, MockObjects.user);
        assertEquals(s.getRelationship().uid, MockObjects.relationship);
        assertEquals(s.getFrom().uid, MockObjects.teiUid);
        assertEquals(s.getTo().uid, MockObjects.teiUid2);
        assertEquals(s.getRelationshipType().uid, MockObjects.relationshipType);
    }

    @Test
    public void convertDeletion() throws Exception {
        DeleteSMSSubmission s = (DeleteSMSSubmission) convert(new QrCodeCase(testLocalDb)
                .generateDeletionCode(MockObjects.eventUid));
        assertEquals(s.getUserID().uid, MockObjects.user);
        assertEquals(s.getEvent().uid, MockObjects.eventUid);
    }

    private SMSSubmission convert(Single<String> task) throws Exception {
        AtomicReference<String> result = new AtomicReference<>();
        task.test()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue(value -> {
                    result.set(value);
                    return !value.isEmpty();
                });

        assertNotNull(result.get());
        byte[] smsBytes = Base64.getDecoder().decode(result.get());
        SMSSubmissionReader reader = new SMSSubmissionReader();
        SMSSubmission subm = reader.readSubmission(smsBytes, new MockMetadata());
        assertNotNull(subm);
        return subm;
    }

    private boolean containsTeiDataValue(ArrayList<TrackedEntityDataValue> values,
                                         SMSDataValue item) {
        for (TrackedEntityDataValue value : values) {
            if (Objects.equals(value.dataElement(), item.getDataElement().uid) &&
                    Objects.equals(value.value(), item.getValue())) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAttributeValue(ArrayList<TrackedEntityAttributeValue> values,
                                           SMSAttributeValue item) {
        for (TrackedEntityAttributeValue value : values) {
            if (Objects.equals(value.trackedEntityAttribute(), item.getAttribute().uid) &&
                    Objects.equals(value.value(), item.getValue())) {
                return true;
            }
        }
        return false;
    }

    private boolean containsDataValue(ArrayList<DataValue> values, SMSDataValue item) {
        for (DataValue value : values) {
            if (Objects.equals(value.dataElement(), item.getDataElement().uid) &&
                    Objects.equals(value.value(), item.getValue())) {
                return true;
            }
        }
        return false;
    }
}
