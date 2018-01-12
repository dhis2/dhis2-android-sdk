package org.hisp.dhis.android.core.common.audit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import org.hamcrest.CoreMatchers;
import org.hisp.dhis.android.core.data.audit.MetadataAudit;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.amqp.EmbeddedAmqpBroker;
import org.hisp.dhis.android.core.data.server.amqp.MetadataAuditMockPublisher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

@RunWith(Parameterized.class)
public class MetadataAuditConsumerMockIntegrationShould {

    @Parameters(name = "{index} MetadataChangeConsumer should return: {0},{1},{2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {IllegalArgumentException.class, "metadata.noExistsKey.create.ApToHMl1NnE",
                        "audit/relationshipType_create.json"},
                {MetadataAudit.class, "metadata.trackedEntity.create.Tc6n4X2NjUt",
                        "audit/trackedEntity_create.json"},
                {MetadataAudit.class, "metadata.relationshipType.create.ApToHMl1NnE",
                        "audit/relationshipType_create.json"},
        });
    }

    private MetadataChangeConsumer consumer;
    private MetadataAuditMockPublisher mockPublisher;
    private EmbeddedAmqpBroker embeddedAmqpBroker;

    private final CountDownLatch lock = new CountDownLatch(1);

    private String routingKey;
    private String fileName;
    private Class<?> expectedResult;

    private Class<?> result;

    public MetadataAuditConsumerMockIntegrationShould(
            Class<?> expectedResult, String routingKey, String fileName) {
        this.expectedResult = expectedResult;
        this.routingKey = routingKey;
        this.fileName = fileName;
    }

    @Before
    public void setUp() throws Exception {
        AmpqConfiguration ampqConfiguration =
                AmpqConfiguration.builder()
                        .setHost("localhost")
                        .setVirtualHost("/")
                        .setUsername("guest")
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
    public void return_result_according_to_parameter_test() throws Exception {
        consumer.setMetadataChangeHandler(new MetadataChangeConsumer.MetadataChangeHandler() {
            @Override
            public void handle(MetadataAudit metadataAudit) {
                result = metadataAudit.getClass();
                lock.countDown();
            }

            @Override
            public void error(Throwable throwable) {
                result = throwable.getClass();
                lock.countDown();
            }
        });

        mockPublisher.publish(routingKey, fileName);

        lock.await();

        assertThat(result, is(notNullValue()));
        assertThat(result, is(CoreMatchers.<Class<?>>equalTo(expectedResult)));
    }
}
