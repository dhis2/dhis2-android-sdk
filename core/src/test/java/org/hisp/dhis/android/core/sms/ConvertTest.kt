/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.sms

import io.reactivex.Single
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.sms.domain.interactor.QrCodeCase
import org.hisp.dhis.android.core.sms.mockrepos.MockLocalDbRepository
import org.hisp.dhis.android.core.sms.mockrepos.testobjects.MockMetadata
import org.hisp.dhis.android.core.sms.mockrepos.testobjects.MockObjects
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.systeminfo.SMSVersion
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.smscompression.SMSSubmissionReader
import org.hisp.dhis.smscompression.models.AggregateDatasetSMSSubmission
import org.hisp.dhis.smscompression.models.DeleteSMSSubmission
import org.hisp.dhis.smscompression.models.EnrollmentSMSSubmission
import org.hisp.dhis.smscompression.models.RelationshipSMSSubmission
import org.hisp.dhis.smscompression.models.SMSAttributeValue
import org.hisp.dhis.smscompression.models.SMSDataValue
import org.hisp.dhis.smscompression.models.SMSSubmission
import org.hisp.dhis.smscompression.models.SimpleEventSMSSubmission
import org.hisp.dhis.smscompression.models.TrackerEventSMSSubmission
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.Base64
import java.util.concurrent.atomic.AtomicReference

@RunWith(JUnit4::class)
class ConvertTest {
    private lateinit var testLocalDb: MockLocalDbRepository

    @Mock
    private lateinit var dhisVersionManager: DHISVersionManager

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        testLocalDb = MockLocalDbRepository()
        `when`(dhisVersionManager.getSmsVersion()).thenReturn(SMSVersion.V2)
    }

    @Test
    fun convertEnrollment() {
        val s = convert(
            QrCodeCase(testLocalDb, dhisVersionManager).generateEnrollmentCode(MockObjects.enrollmentUid),
        ) as EnrollmentSMSSubmission
        assertEquals(s.userID.uid, MockObjects.user)
        assertEquals(s.enrollment.uid, MockObjects.enrollmentUid)
        assertEquals(s.enrollmentDate, MockObjects.enrollmentDate)
        assertEquals(s.enrollmentStatus.name, MockObjects.enrollmentStatus.name)
        assertEquals(s.incidentDate, MockObjects.incidentDate)
        assertEquals(s.orgUnit.uid, MockObjects.orgUnit)
        assertEquals(s.trackerProgram.uid, MockObjects.program)
        assertEquals(s.trackedEntityType.uid, MockObjects.trackedEntityType)
        assertEquals(s.trackedEntityInstance.uid, MockObjects.teiUid)
        assertEquals(s.coordinates.latitude.toDouble(), MockObjects.latitude.toDouble(), 0.0001)
        assertEquals(s.coordinates.longitude.toDouble(), MockObjects.longitude.toDouble(), 0.0001)

        for (item in s.values) {
            assertTrue(containsAttributeValue(MockObjects.getTestAttributeValues(), item))
        }

        assertEquals(s.events.size, 1)
        val event = s.events[0]
        assertEquals(event.event.uid, MockObjects.eventUid)
        assertEquals(event.attributeOptionCombo.uid, MockObjects.attributeOptionCombo)
        assertEquals(event.eventDate, MockObjects.eventDate)
        assertEquals(event.dueDate, MockObjects.dueDate)
        assertEquals(event.eventStatus.name, MockObjects.eventStatus.name)
        assertEquals(event.orgUnit.uid, MockObjects.orgUnit)
        for (item in event.values) {
            assertTrue(containsTeiDataValue(MockObjects.getTeiDataValues(), item))
        }
    }

    @Test
    fun convertEnrollmentWitNullEvent() {
        val s = convert(
            QrCodeCase(testLocalDb, dhisVersionManager)
                .generateEnrollmentCode(MockObjects.enrollmentUidWithNullEvents),
        ) as EnrollmentSMSSubmission
        assertNull(s.events)
    }

    @Test
    fun convertEnrollmentWithEmptyEventList() {
        val s = convert(
            QrCodeCase(testLocalDb, dhisVersionManager)
                .generateEnrollmentCode(MockObjects.enrollmentUidWithoutEvents),
        ) as EnrollmentSMSSubmission
        assertNull(s.events)
    }

    @Test
    fun convertEnrollmentWithoutGeometry() {
        val s = convert(
            QrCodeCase(testLocalDb, dhisVersionManager)
                .generateEnrollmentCode(MockObjects.enrollmentUidWithoutGeometry),
        ) as EnrollmentSMSSubmission
        assertNull(s.coordinates)
    }

    @Test
    fun convertSimpleEvent() {
        val s = convert(
            QrCodeCase(testLocalDb, dhisVersionManager).generateSimpleEventCode(MockObjects.eventUid),
        ) as SimpleEventSMSSubmission
        assertEquals(s.userID.uid, MockObjects.user)
        assertEquals(s.event.uid, MockObjects.eventUid)
        assertEquals(s.eventDate, MockObjects.eventDate)
        assertEquals(s.eventStatus.name, MockObjects.eventStatus.name)
        assertEquals(s.eventProgram.uid, MockObjects.program)
        assertEquals(s.dueDate, MockObjects.dueDate)
        assertEquals(s.attributeOptionCombo.uid, MockObjects.attributeOptionCombo)
        assertEquals(s.orgUnit.uid, MockObjects.orgUnit)
        assertEquals(s.coordinates.latitude.toDouble(), MockObjects.latitude.toDouble(), 0.0001)
        assertEquals(s.coordinates.longitude.toDouble(), MockObjects.longitude.toDouble(), 0.0001)
        for (item in s.values) {
            assertTrue(containsTeiDataValue(MockObjects.getTeiDataValues(), item))
        }
    }

    @Test
    fun convertTrackerEvent() {
        val s = convert(
            QrCodeCase(testLocalDb, dhisVersionManager).generateTrackerEventCode(MockObjects.eventUid),
        ) as TrackerEventSMSSubmission
        assertEquals(s.userID.uid, MockObjects.user)
        assertEquals(s.event.uid, MockObjects.eventUid)
        assertEquals(s.eventDate, MockObjects.eventDate)
        assertEquals(s.eventStatus.name, MockObjects.eventStatus.name)
        assertEquals(s.programStage.uid, MockObjects.programStage)
        assertEquals(s.dueDate, MockObjects.dueDate)
        assertEquals(s.attributeOptionCombo.uid, MockObjects.attributeOptionCombo)
        assertEquals(s.orgUnit.uid, MockObjects.orgUnit)
        assertEquals(s.enrollment.uid, MockObjects.enrollmentUid)
        assertEquals(s.coordinates.latitude.toDouble(), MockObjects.latitude.toDouble(), 0.0001)
        assertEquals(s.coordinates.longitude.toDouble(), MockObjects.longitude.toDouble(), 0.0001)
        for (item in s.values) {
            assertTrue(containsTeiDataValue(MockObjects.getTeiDataValues(), item))
        }
    }

    @Test
    fun convertDataSet() {
        val s = convert(
            QrCodeCase(testLocalDb, dhisVersionManager).generateDataSetCode(
                MockObjects.dataSetUid,
                MockObjects.orgUnit,
                MockObjects.period,
                MockObjects.attributeOptionCombo,
            ),
        ) as AggregateDatasetSMSSubmission
        assertEquals(s.userID.uid, MockObjects.user)
        assertEquals(s.orgUnit.uid, MockObjects.orgUnit)
        assertEquals(s.attributeOptionCombo.uid, MockObjects.attributeOptionCombo)
        assertEquals(s.dataSet.uid, MockObjects.dataSetUid)
        assertEquals(s.period, MockObjects.period)

        for (item in s.values) {
            assertTrue(containsDataValue(MockObjects.getDataValues(), item))
        }
        assertEquals(s.isComplete, MockObjects.isCompleted)
    }

    // TODO Enable this test when the compression supports empty lists
    // @Test
    fun convertDataSetWithEmptyDataValueList() {
        val s = convert(
            QrCodeCase(testLocalDb, dhisVersionManager).generateDataSetCode(
                MockObjects.dataSetEmptyListUid,
                MockObjects.orgUnit,
                MockObjects.period,
                MockObjects.attributeOptionCombo,
            ),
        ) as AggregateDatasetSMSSubmission
        assertEquals(s.userID.uid, MockObjects.user)
        assertEquals(s.orgUnit.uid, MockObjects.orgUnit)
        assertEquals(s.attributeOptionCombo.uid, MockObjects.attributeOptionCombo)
        assertEquals(s.dataSet.uid, MockObjects.dataSetUid)
        assertEquals(s.period, MockObjects.period)

        assertEquals(s.values.size, 0)
        assertEquals(s.isComplete, MockObjects.isCompleted)
    }

    @Test
    fun convertRelationship() {
        val s = convert(
            QrCodeCase(testLocalDb, dhisVersionManager).generateRelationshipCode(MockObjects.relationship),
        ) as RelationshipSMSSubmission
        assertEquals(s.userID.uid, MockObjects.user)
        assertEquals(s.relationship.uid, MockObjects.relationship)
        assertEquals(s.from.uid, MockObjects.teiUid)
        assertEquals(s.to.uid, MockObjects.teiUid2)
        assertEquals(s.relationshipType.uid, MockObjects.relationshipType)
    }

    @Test
    fun convertDeletion() {
        val s = convert(
            QrCodeCase(testLocalDb, dhisVersionManager).generateDeletionCode(MockObjects.eventUid),
        ) as DeleteSMSSubmission
        assertEquals(s.userID.uid, MockObjects.user)
        assertEquals(s.event.uid, MockObjects.eventUid)
    }

    private fun convert(task: Single<String>): SMSSubmission {
        val result = AtomicReference<String>()
        task.test()
            .assertNoErrors()
            .assertValueCount(1)
            .assertValue { value ->
                result.set(value)
                value.isNotEmpty()
            }

        assertNotNull(result.get())
        val smsBytes = Base64.getDecoder().decode(result.get())
        val reader = SMSSubmissionReader()
        val subm = reader.readSubmission(smsBytes, MockMetadata())
        assertNotNull(subm)
        return subm
    }

    private fun containsTeiDataValue(values: List<TrackedEntityDataValue>, item: SMSDataValue): Boolean {
        return values.any { value ->
            value.dataElement() == item.dataElement.uid && value.value() == item.value
        }
    }

    private fun containsAttributeValue(values: List<TrackedEntityAttributeValue>, item: SMSAttributeValue): Boolean {
        return values.any { value ->
            value.trackedEntityAttribute() == item.attribute.uid && value.value() == item.value
        }
    }

    private fun containsDataValue(values: Collection<DataValue>, item: SMSDataValue): Boolean {
        return values.any { value ->
            value.dataElement() == item.dataElement.uid && value.value() == item.value
        }
    }
}
