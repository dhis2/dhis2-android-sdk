package org.hisp.dhis.android.core.audit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import org.hamcrest.CoreMatchers;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

@RunWith(Parameterized.class)
public class MetadataAuditConsumerMockIntegrationShould {

    @Parameters(name = "{index} MetadataAuditConsumer should return: {0},{1},{2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {IllegalArgumentException.class, "metadata.noExistsKey.create.ApToHMl1NnE",
                        "audit/relationshipType_create.json"},
                {MetadataAudit.class, "metadata.trackedEntity.create.Tc6n4X2NjUt",
                        "audit/trackedEntity_create.json"},
                {MetadataAudit.class, "metadata.relationshipType.create.ApToHMl1NnE",
                        "audit/relationshipType_create.json"},
                {MetadataAudit.class, "metadata.optionSet.create.qy1kWnfUk8b",
                        "audit/optionSet_create.json"},
                {MetadataAudit.class, "metadata.option.create.eHqt440U096",
                        "audit/option_create.json"},
                {MetadataAudit.class, "metadata.program.create.CtLttUVOkea",
                        "audit/program_create.json"},
                {MetadataAudit.class, "metadata.programStage.create.hKuTxVbPcci",
                        "audit/programStage_create.json"},
                {MetadataAudit.class, "metadata.programIndicator.create.bG61Yg5Vx43",
                        "audit/programIndicator_create.json"},
                {MetadataAudit.class, "metadata.programRule.create.lpAbEkA12h0",
                        "audit/programRule_create.json"},
                {MetadataAudit.class, "metadata.programRuleAction.create.yCTGPpo27M0",
                        "audit/programRuleAction_create.json"},
                {MetadataAudit.class, "metadata.programRuleVariable.create.sm4OWIjiY6K",
                        "audit/programRuleVariable_create.json"},
                {MetadataAudit.class, "metadata.dataElement.create.SKPMfGShCt5",
                        "audit/data_element_create.json"},
                {MetadataAudit.class, "metadata.trackedEntityAttribute.create.bNNXBXTGDVV",
                        "audit/tracked_entity_attribute_create.json"}
        });
    }

    private MetadataAuditConsumer consumer;
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
        MetadataAuditConnection metadataAuditConnection =
                MetadataAuditConnection.builder()
                        .setHost("localhost")
                        .setVirtualHost("/")
                        .setUsername("guest")
                        .setPassword("guest")
                        .setPort(5672)
                        .build();

        embeddedAmqpBroker = new EmbeddedAmqpBroker();
        embeddedAmqpBroker.start(String.valueOf(metadataAuditConnection.port()));

        consumer = new MetadataAuditConsumer(metadataAuditConnection);
        consumer.start();

        mockPublisher = new MetadataAuditMockPublisher(metadataAuditConnection,
                new ResourcesFileReader());
        mockPublisher.start();
    }

    @After
    public void tearDown() throws Exception {
        consumer.stop();
        mockPublisher.stop();
        embeddedAmqpBroker.stop();

    }

    @Test
    public void return_result_according_to_parameter_test() throws Exception {
        consumer.setMetadataAuditListener(new MetadataAuditConsumer.MetadataAuditListener() {
            @Override
            public void onMetadataChanged(Class<?> klass, MetadataAudit metadataAudit) {
                result = metadataAudit.getClass();
                lock.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
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
