package org.hisp.dhis.android.core;

import android.support.annotation.NonNull;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.net.URL;

public class RabbitMQReceiver {
    private final static String QUEUE_NAME = "dhis2";

    public RabbitMQReceiver(URL url)
            throws Exception {
        ConnectionFactory factory = setupFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("[x] Received '" + message + "'");
            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

    @NonNull
    private ConnectionFactory setupFactory() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setUsername("guest2");
        factory.setPassword("guest2");
        factory.setVirtualHost("/");
        factory.setHost("192.168.0.169");
        factory.setPort(5672);

        return factory;
    }
}
