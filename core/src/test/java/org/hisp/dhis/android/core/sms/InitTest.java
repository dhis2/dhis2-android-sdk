package org.hisp.dhis.android.core.sms;

import org.hisp.dhis.android.core.sms.domain.interactor.InitCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RunWith(JUnit4.class)
public class InitTest {

    @Test
    public void allData() {
        String testGateway = "testGateway";
        String testConfirmationNumber = "testConfirmationNumber";
        TestRepositories.TestLocalDbRepository testLocalDbRepository = new TestRepositories.TestLocalDbRepository();

        new InitCase(testLocalDbRepository)
                .initSMSModule(testGateway, testConfirmationNumber)
                .test()
                .awaitDone(3, TimeUnit.SECONDS)
                .assertNoErrors();

        testLocalDbRepository.getGatewayNumber().test().assertNoErrors().assertValue(testGateway);
        testLocalDbRepository.getConfirmationSenderNumber().test().assertNoErrors().assertValue(testConfirmationNumber);
    }

    @Test
    public void emptyData() {
        TestRepositories.TestLocalDbRepository testLocalDbRepository = new TestRepositories.TestLocalDbRepository();

        new InitCase(testLocalDbRepository)
                .initSMSModule(null, null)
                .test()
                .awaitDone(3, TimeUnit.SECONDS)
                .assertError(IllegalArgumentException.class);
    }

    @Test
    public void onlyGateway() {
        String testGateway = "testGateway";
        TestRepositories.TestLocalDbRepository testLocalDbRepository = new TestRepositories.TestLocalDbRepository();

        new InitCase(testLocalDbRepository)
                .initSMSModule(testGateway, null)
                .test()
                .awaitDone(3, TimeUnit.SECONDS)
                .assertNoErrors();

        testLocalDbRepository.getGatewayNumber().test().assertNoErrors().assertValue(testGateway);
        testLocalDbRepository.getConfirmationSenderNumber().test().assertError(Objects::nonNull);
    }
}
