package org.hisp.dhis.android.core.sms;

import org.hisp.dhis.android.core.sms.domain.interactor.SmsSubmitCase;
import org.hisp.dhis.android.core.sms.mockrepos.MockDeviceStateRepository;
import org.hisp.dhis.android.core.sms.mockrepos.MockLocalDbRepository;
import org.hisp.dhis.android.core.sms.mockrepos.MockSmsRepository;
import org.hisp.dhis.android.core.sms.mockrepos.testobjects.MockObjects;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.Callable;

import io.reactivex.Single;

@RunWith(JUnit4.class)
public class SubmitTest {
    private MockLocalDbRepository localDbRepository;
    private MockDeviceStateRepository deviceStateRepository;
    private MockSmsRepository smsRepository;
    private SmsSubmitCase sender;

    @Before
    public void init() {
        localDbRepository = new MockLocalDbRepository();
        deviceStateRepository = new MockDeviceStateRepository();
        smsRepository = new MockSmsRepository();
        sender = new SmsSubmitCase(localDbRepository, smsRepository, deviceStateRepository);
    }

    @Test
    public void submitEnrollment() throws Exception {
        test(() -> sender.convertEnrollment(MockObjects.enrollmentUid));
    }

    @Test
    public void submitTrackerEvent() throws Exception {
        test(() -> sender.convertTrackerEvent(MockObjects.eventUid));
    }

    @Test
    public void submitSimpleEvent() throws Exception {
        test(() -> sender.convertSimpleEvent(MockObjects.eventUid));
    }

    @Test
    public void submitDataSet() throws Exception {
        test(() -> sender.convertDataSet(
                MockObjects.dataSetUid, MockObjects.orgUnit,
                MockObjects.period, MockObjects.attributeOptionCombo));
    }

    @Test
    public void submitRelationShip() throws Exception {
        test(() -> sender.convertRelationship(MockObjects.relationship));
    }

    @Test
    public void submitDeletion() throws Exception {
        test(() -> sender.convertDeletion(MockObjects.eventUid));
    }

    private void test(Callable<Single<Integer>> convertTask) throws Exception {
        convertTask.call().test()
                .assertNoErrors()
                .assertValueCount(1);
        sender.send().test()
                .assertNoErrors()
                .assertValueCount(2);

        localDbRepository.setGatewayNumber("").test().assertComplete();
        sender = new SmsSubmitCase(localDbRepository, smsRepository, deviceStateRepository);
        convertTask.call().test()
                .assertError(error -> error instanceof SmsSubmitCase.PreconditionFailed &&
                        ((SmsSubmitCase.PreconditionFailed) error).getType() == SmsSubmitCase.PreconditionFailed.Type.NO_GATEWAY_NUMBER_SET);

        localDbRepository = new MockLocalDbRepository();
        localDbRepository.setModuleEnabled(false).test().assertComplete();
        sender = new SmsSubmitCase(localDbRepository, smsRepository, deviceStateRepository);
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
        sender = new SmsSubmitCase(localDbRepository, smsRepository, deviceStateRepository);
        convertTask.call().test()
                .assertError(error -> error instanceof SmsSubmitCase.PreconditionFailed &&
                        ((SmsSubmitCase.PreconditionFailed) error).getType() == SmsSubmitCase.PreconditionFailed.Type.NO_NETWORK);
    }
}
