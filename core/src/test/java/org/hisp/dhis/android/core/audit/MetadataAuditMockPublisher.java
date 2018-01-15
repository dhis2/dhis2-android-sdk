package org.hisp.dhis.android.core.audit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.hisp.dhis.android.core.data.file.IFileReader;

public class MetadataAuditMockPublisher {
    private static final String EXCHANGE_NAME = "dhis2";

    private MetadataAuditConnection metadataAuditConnection;
    private IFileReader fileReader;

    private Connection connection;
    private Channel channel;

    public MetadataAuditMockPublisher(MetadataAuditConnection metadataAuditConnection,
            IFileReader fileReader) {
        this.metadataAuditConnection = metadataAuditConnection;
        this.fileReader = fileReader;
    }

    public void start() throws Exception {
        connectToMessageBroker();
    }

    public void stop() throws Exception {
        connection.close();
    }

    public void publish(String routingKey, String fileName) throws Exception {
        String message = fileReader.getStringFromFile(fileName);

        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
    }

    private void connectToMessageBroker() throws Exception {
        ConnectionFactory factory = setupFactory();

        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
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
}
