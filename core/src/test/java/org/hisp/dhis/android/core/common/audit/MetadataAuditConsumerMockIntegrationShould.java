package org.hisp.dhis.android.core.common.audit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import org.hisp.dhis.android.core.data.audit.MetadataAudit;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.amqp.EmbeddedAmqpBroker;
import org.hisp.dhis.android.core.data.server.amqp.MetadataAuditMockPublisher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class MetadataAuditConsumerMockIntegrationShould {

    MetadataChangeConsumer consumer;
    MetadataAuditMockPublisher mockPublisher;
    EmbeddedAmqpBroker embeddedAmqpBroker;

    private final CountDownLatch lock = new CountDownLatch(1);

    MetadataAudit metadataAuditInfo;

    @Before
    public void setUp() throws Exception {
        AmpqConfiguration ampqConfiguration =
                AmpqConfiguration.builder()
                        .setHost("localhost")
                        .setVirtualHost("/")
                        .setUserName("guest")
                        .setPassword("guest")
                        .setPort(5672)
                        .build();

        embeddedAmqpBroker = new EmbeddedAmqpBroker();
        embeddedAmqpBroker.start(String.valueOf(ampqConfiguration.port()));

        consumer = new MetadataChangeConsumer(ampqConfiguration);
        mockPublisher = new MetadataAuditMockPublisher(ampqConfiguration,
                new ResourcesFileReader());
    }

    @After
    public void tearDown() throws IOException {
        consumer.close();
        mockPublisher.close();
        embeddedAmqpBroker.stop();

    }

    @Test
    public void return_metadata_change_message() throws Exception {

        consumer.setMetadataChangeHandler(new MetadataChangeConsumer.MetadataChangeHandler() {
            @Override
            public void handle(MetadataAudit metadataAudit) {
                metadataAuditInfo = metadataAudit;
                lock.countDown();
            }
        });

        mockPublisher.publish(
                "metadata.trackedEntity.create.Tc6n4X2NjUt",
                "audit/createTrackedEntity.json");

        lock.await();

        assertThat(metadataAuditInfo, is(notNullValue()));
    }
}
