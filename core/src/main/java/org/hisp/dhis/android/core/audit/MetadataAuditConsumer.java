package org.hisp.dhis.android.core.audit;

import android.util.Log;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.regex.Pattern;

public class MetadataAuditConsumer {
    private final static String EXCHANGE_NAME = "dhis2";
    private final MetadataAuditConnection metadataAuditConnection;

    private Connection connection;
    private Channel channel;
    private String queueName;

    private MetadataAuditListener metadataAuditListener;

    public MetadataAuditConsumer(MetadataAuditConnection metadataAuditConnection) {

        this.metadataAuditConnection = metadataAuditConnection;
    }

    public void setMetadataAuditListener(MetadataAuditListener metadataAuditListener) {
        this.metadataAuditListener = metadataAuditListener;
    }

    public void start() throws Exception {
        connectToMessageBroker();
        createConsumer();
    }

    public void stop() throws Exception {
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

        factory.setUsername(metadataAuditConnection.username());
        factory.setPassword(metadataAuditConnection.password());
        factory.setVirtualHost(metadataAuditConnection.virtualHost());
        factory.setHost(metadataAuditConnection.host());
        factory.setPort(metadataAuditConnection.port());

        return factory;
    }

    private void createConsumer() throws IOException {
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");

                if (metadataAuditListener != null) {
                    try {
                        Class<?> klass = MetadataClassFactory.getByName(
                                envelope.getRoutingKey().split(Pattern.quote("."))[1]);

                        GenericClassParser genericClassParser = new GenericClassParser();
                        MetadataAudit metadataAudit = genericClassParser.parse(message,
                                MetadataAudit.class, klass);

                        metadataAuditListener.onMetadataChanged(klass, metadataAudit);
                    } catch (Exception e) {
                        metadataAuditListener.onError(e);
                        Log.e(this.getClass().getSimpleName(), e.getMessage());
                    }
                }
            }
        };

        channel.basicConsume(queueName, true, consumer);
    }

    public interface MetadataAuditListener {
        void onMetadataChanged(Class<?> klass, MetadataAudit metadataAudit);

        void onError(Throwable throwable);
    }
}
