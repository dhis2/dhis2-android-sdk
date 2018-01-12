package org.hisp.dhis.android.core.common.audit;

import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.audit.MetadataAudit;

import java.io.IOException;
import java.util.regex.Pattern;

public class MetadataChangeConsumer {
    private final static String EXCHANGE_NAME = "dhis2";
    private final AmpqConfiguration ampqConfiguration;

    private Connection connection;
    private Channel channel;
    private String queueName;

    private MetadataChangeHandler metadataChangeHandler;

    public MetadataChangeConsumer(AmpqConfiguration ampqConfiguration) throws Exception {

        this.ampqConfiguration = ampqConfiguration;

        connectToMessageBroker();
    }

    public void setMetadataChangeHandler(MetadataChangeHandler metadataChangeHandler)
            throws IOException {
        this.metadataChangeHandler = metadataChangeHandler;

        createConsumer();
    }

    public void close() throws IOException {
        connection.close();
    }

    private void connectToMessageBroker() throws Exception {
        ConnectionFactory factory = setupFactory();
        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
        queueName = channel.queueDeclare().getQueue();

        channel.queueBind(queueName, EXCHANGE_NAME, "metadata.#");
    }

    private ConnectionFactory setupFactory() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setUsername(ampqConfiguration.username());
        factory.setPassword(ampqConfiguration.password());
        factory.setVirtualHost(ampqConfiguration.virtualHost());
        factory.setHost(ampqConfiguration.host());
        factory.setPort(ampqConfiguration.port());

        return factory;
    }

    private void createConsumer() throws IOException {
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");

                if (metadataChangeHandler != null) {
                    try {
                        metadataChangeHandler.handle(
                                parseMetadataAudit(envelope.getRoutingKey(), message));
                    } catch (Exception e) {
                        metadataChangeHandler.error(e);
                        Log.e(this.getClass().getSimpleName(), e.getMessage());
                    }
                }
            }
        };

        channel.basicConsume(queueName, true, consumer);
    }

    private MetadataAudit parseMetadataAudit(String routingKey, String body)
            throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        JavaType type = getType(routingKey.split(Pattern.quote("."))[1], objectMapper);

        return objectMapper.readValue(body, type);
    }

    private JavaType getType(String className, ObjectMapper objectMapper) {

        Class<?> klass = MetadataClassFactory.getByName(className);

        return objectMapper.getTypeFactory()
                .constructParametricType(MetadataAudit.class, klass);
    }

    public interface MetadataChangeHandler {
        void handle(MetadataAudit metadataAudit);

        void error(Throwable throwable);
    }
}
