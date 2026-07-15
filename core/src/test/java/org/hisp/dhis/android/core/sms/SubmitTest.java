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

package org.hisp.dhis.android.core.sms;

import org.hisp.dhis.android.core.sms.domain.interactor.SmsSubmitCase;
import org.hisp.dhis.android.core.sms.mockrepos.MockDeviceStateRepository;
import org.hisp.dhis.android.core.sms.mockrepos.MockLocalDbRepository;
import org.hisp.dhis.android.core.sms.mockrepos.MockSmsRepository;
import org.hisp.dhis.android.core.sms.mockrepos.testobjects.MockObjects;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.systeminfo.SMSVersion;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Callable;

import io.reactivex.Single;

import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class SubmitTest {
    private MockLocalDbRepository localDbRepository;
    private MockDeviceStateRepository deviceStateRepository;
    private MockSmsRepository smsRepository;
    private SmsSubmitCase sender;

    @Mock
    private DHISVersionManager dhisVersionManager;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        localDbRepository = new MockLocalDbRepository();
        deviceStateRepository = new MockDeviceStateRepository();
        smsRepository = new MockSmsRepository();

        when(dhisVersionManager.getSmsVersion()).thenReturn(SMSVersion.V2);
        sender = new SmsSubmitCase(localDbRepository, smsRepository, deviceStateRepository, dhisVersionManager);
    }

    @Test
    public void submitEnrollment() throws Exception {
        testConvert(() -> sender.convertEnrollment(MockObjects.enrollmentUid));
    }

    @Test
    public void compressEnrollment() throws Exception {
        testCompress(() -> sender.compressEnrollment(MockObjects.enrollmentUid));
    }

    @Test
    public void submitTrackerEvent() throws Exception {
        testConvert(() -> sender.convertTrackerEvent(MockObjects.eventUid));
    }

    @Test
    public void compressTrackerEvent() throws Exception {
        testCompress(() -> sender.compressTrackerEvent(MockObjects.eventUid));
    }

    @Test
    public void submitSimpleEvent() throws Exception {
        testConvert(() -> sender.convertSimpleEvent(MockObjects.eventUid));
    }

    @Test
    public void compressSimpleEvent() throws Exception {
        testCompress(() -> sender.compressSimpleEvent(MockObjects.eventUid));
    }

    @Test
    public void submitDataSet() throws Exception {
        testConvert(() -> sender.convertDataSet(
                MockObjects.dataSetUid, MockObjects.orgUnit,
                MockObjects.period, MockObjects.attributeOptionCombo));
    }

    @Test
    public void compressDataSet() throws Exception {
        testCompress(() -> sender.compressDataSet(
                MockObjects.dataSetUid, MockObjects.orgUnit,
                MockObjects.period, MockObjects.attributeOptionCombo));
    }

    @Test
    public void submitRelationShip() throws Exception {
        testConvert(() -> sender.convertRelationship(MockObjects.relationship));
    }

    @Test
    public void compressRelationShip() throws Exception {
        testCompress(() -> sender.compressRelationship(MockObjects.relationship));
    }

    @Test
    public void submitDeletion() throws Exception {
        testConvert(() -> sender.convertDeletion(MockObjects.eventUid));
    }

    @Test
    public void compressDeletion() throws Exception {
        testCompress(() -> sender.compressDeletion(MockObjects.eventUid));
    }

    private void testConvert(Callable<Single<Integer>> convertTask) throws Exception {
        convertTask.call().test()
                .assertNoErrors()
                .assertValueCount(1);
        sender.send().test()
                .assertNoErrors()
                .assertValueCount(2);

        localDbRepository.setGatewayNumber("").test().assertComplete();
        sender = new SmsSubmitCase(localDbRepository, smsRepository, deviceStateRepository, dhisVersionManager);
        convertTask.call().test()
                .assertError(error -> error instanceof SmsSubmitCase.PreconditionFailed &&
                        ((SmsSubmitCase.PreconditionFailed) error).getType() == SmsSubmitCase.PreconditionFailed.Type.NO_GATEWAY_NUMBER_SET);

        localDbRepository = new MockLocalDbRepository();
        localDbRepository.setModuleEnabled(false).test().assertComplete();
        sender = new SmsSubmitCase(localDbRepository, smsRepository, deviceStateRepository, dhisVersionManager);
        convertTask.call().test()
                .assertError(error -> error instanceof SmsSubmitCase.PreconditionFailed &&
                        ((SmsSubmitCase.PreconditionFailed) error).getType() == SmsSubmitCase.PreconditionFailed.Type.SMS_MODULE_DISABLED);

        localDbRepository = new MockLocalDbRepository();
        deviceStateRepository = new MockDeviceStateRepository() {
            @Override
            public Single<Boolean> isNetworkConnected() {
                return Single.just(false);
            }
        };
        sender = new SmsSubmitCase(localDbRepository, smsRepository, deviceStateRepository, dhisVersionManager);
        convertTask.call().test()
                .assertError(error -> error instanceof SmsSubmitCase.PreconditionFailed &&
                        ((SmsSubmitCase.PreconditionFailed) error).getType() == SmsSubmitCase.PreconditionFailed.Type.NO_NETWORK);
    }

    private void testCompress(Callable<Single<String>> compressTask) throws Exception {
        compressTask.call().test()
                .assertNoErrors()
                .assertValueCount(1);
    }
}
