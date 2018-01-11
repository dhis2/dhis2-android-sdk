package org.hisp.dhis.android.core.data.server.amqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.hisp.dhis.android.core.common.audit.AmpqConfiguration;
import org.hisp.dhis.android.core.data.file.IFileReader;

import java.io.IOException;

public class MetadataAuditMockPublisher {
    private static final String EXCHANGE_NAME = "dhis2";

    private AmpqConfiguration ampqConfiguration;
    private IFileReader fileReader;

    private Connection connection;
    private Channel channel;

    public MetadataAuditMockPublisher(AmpqConfiguration ampqConfiguration, IFileReader fileReader)
            throws Exception {
        this.ampqConfiguration = ampqConfiguration;
        this.fileReader = fileReader;

        connectToMessageBroker();
    }

    public void close() throws IOException {
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

        factory.setUsername(ampqConfiguration.userName());
        factory.setPassword(ampqConfiguration.password());
        factory.setVirtualHost(ampqConfiguration.virtualHost());
        factory.setHost(ampqConfiguration.host());
        factory.setPort(ampqConfiguration.port());

        return factory;
    }
}
