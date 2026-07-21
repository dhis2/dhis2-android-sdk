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
import org.hisp.dhis.android.core.sms.domain.interactor.SmsSubmitCase
import org.hisp.dhis.android.core.sms.mockrepos.MockDeviceStateRepository
import org.hisp.dhis.android.core.sms.mockrepos.MockLocalDbRepository
import org.hisp.dhis.android.core.sms.mockrepos.MockSmsRepository
import org.hisp.dhis.android.core.sms.mockrepos.testobjects.MockObjects
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.systeminfo.SMSVersion
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class SubmitTest {
    private lateinit var localDbRepository: MockLocalDbRepository
    private lateinit var deviceStateRepository: MockDeviceStateRepository
    private lateinit var smsRepository: MockSmsRepository
    private lateinit var sender: SmsSubmitCase

    @Mock
    private lateinit var dhisVersionManager: DHISVersionManager

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        localDbRepository = MockLocalDbRepository()
        deviceStateRepository = MockDeviceStateRepository()
        smsRepository = MockSmsRepository()

        `when`(dhisVersionManager.getSmsVersion()).thenReturn(SMSVersion.V2)
        sender = SmsSubmitCase(localDbRepository, smsRepository, deviceStateRepository, dhisVersionManager)
    }

    @Test
    fun submitEnrollment() {
        testConvert { sender.convertEnrollment(MockObjects.enrollmentUid) }
    }

    @Test
    fun compressEnrollment() {
        testCompress { sender.compressEnrollment(MockObjects.enrollmentUid) }
    }

    @Test
    fun submitTrackerEvent() {
        testConvert { sender.convertTrackerEvent(MockObjects.eventUid) }
    }

    @Test
    fun compressTrackerEvent() {
        testCompress { sender.compressTrackerEvent(MockObjects.eventUid) }
    }

    @Test
    fun submitSimpleEvent() {
        testConvert { sender.convertSimpleEvent(MockObjects.eventUid) }
    }

    @Test
    fun compressSimpleEvent() {
        testCompress { sender.compressSimpleEvent(MockObjects.eventUid) }
    }

    @Test
    fun submitDataSet() {
        testConvert {
            sender.convertDataSet(
                MockObjects.dataSetUid,
                MockObjects.orgUnit,
                MockObjects.period,
                MockObjects.attributeOptionCombo,
            )
        }
    }

    @Test
    fun compressDataSet() {
        testCompress {
            sender.compressDataSet(
                MockObjects.dataSetUid,
                MockObjects.orgUnit,
                MockObjects.period,
                MockObjects.attributeOptionCombo,
            )
        }
    }

    @Test
    fun submitRelationShip() {
        testConvert { sender.convertRelationship(MockObjects.relationship) }
    }

    @Test
    fun compressRelationShip() {
        testCompress { sender.compressRelationship(MockObjects.relationship) }
    }

    @Test
    fun submitDeletion() {
        testConvert { sender.convertDeletion(MockObjects.eventUid) }
    }

    @Test
    fun compressDeletion() {
        testCompress { sender.compressDeletion(MockObjects.eventUid) }
    }

    private fun testConvert(convertTask: () -> Single<Int>) {
        convertTask().test()
            .assertNoErrors()
            .assertValueCount(1)
        sender.send().test()
            .assertNoErrors()
            .assertValueCount(2)

        localDbRepository.setGatewayNumber("").test().assertComplete()
        sender = SmsSubmitCase(localDbRepository, smsRepository, deviceStateRepository, dhisVersionManager)
        convertTask().test()
            .assertError { error ->
                error is SmsSubmitCase.PreconditionFailed &&
                    error.type == SmsSubmitCase.PreconditionFailed.Type.NO_GATEWAY_NUMBER_SET
            }

        localDbRepository = MockLocalDbRepository()
        localDbRepository.setModuleEnabled(false).test().assertComplete()
        sender = SmsSubmitCase(localDbRepository, smsRepository, deviceStateRepository, dhisVersionManager)
        convertTask().test()
            .assertError { error ->
                error is SmsSubmitCase.PreconditionFailed &&
                    error.type == SmsSubmitCase.PreconditionFailed.Type.SMS_MODULE_DISABLED
            }

        localDbRepository = MockLocalDbRepository()
        deviceStateRepository = object : MockDeviceStateRepository() {
            override fun isNetworkConnected(): Single<Boolean> {
                return Single.just(false)
            }
        }
        sender = SmsSubmitCase(localDbRepository, smsRepository, deviceStateRepository, dhisVersionManager)
        convertTask().test()
            .assertError { error ->
                error is SmsSubmitCase.PreconditionFailed &&
                    error.type == SmsSubmitCase.PreconditionFailed.Type.NO_NETWORK
            }
    }

    private fun testCompress(compressTask: () -> Single<String>) {
        compressTask().test()
            .assertNoErrors()
            .assertValueCount(1)
    }
}
