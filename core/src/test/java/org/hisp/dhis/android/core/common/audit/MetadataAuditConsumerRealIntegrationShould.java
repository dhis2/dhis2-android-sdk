package org.hisp.dhis.android.core.common.audit;

import static junit.framework.Assert.fail;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import org.hisp.dhis.android.core.data.audit.MetadataAudit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class MetadataAuditConsumerRealIntegrationShould {

    MetadataChangeConsumer consumer;

    private final CountDownLatch lock = new CountDownLatch(1);

    MetadataAudit metadataAuditInfo;

    @Before
    public void setUp() throws Exception {
        consumer = new MetadataChangeConsumer(
                AmpqConfiguration.builder()
                        .setHost("192.168.1.42")
                        .setVirtualHost("/")
                        .setUsername("guest2")
                        .setPassword("guest2")
                        .setPort(5672)
                        .build());
    }

    @After
    public void tearDown() throws IOException {
        consumer.close();
    }

    //The goal of this test is research how messages are received from rabbitmq
    //this test must to be commented because need a message broker, rabbitmq configuration
    //in dhis2 server and manual metadata change.MetadataAudit.java
    //@Test
    public void return_metadata_change_message() throws Exception {
        consumer.setMetadataChangeHandler(new MetadataChangeConsumer.MetadataChangeHandler() {
            @Override
            public void handle(MetadataAudit metadataAudit) {
                metadataAuditInfo = metadataAudit;
                lock.countDown();
            }

            @Override
            public void error(Throwable throwable) {
                fail(throwable.getMessage());
                lock.countDown();
            }
        });

        lock.await();

        assertThat(metadataAuditInfo, is(notNullValue()));
    }
}
