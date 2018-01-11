package org.hisp.dhis.android.core.common.ampq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class MetadataChangeConsumer {
    public interface MetadataChangeHandler {
        void handle(String routingKey, String message);
    }

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

        factory.setUsername(ampqConfiguration.userName());
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
                System.out.println(
                        " [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");

                if (metadataChangeHandler != null) {
                    metadataChangeHandler.handle(envelope.getRoutingKey(), message);
                }
            }
        };
        channel.basicConsume(queueName, true, consumer);

        System.out.println(" [*] Waiting for ampq messages.");
    }


}
