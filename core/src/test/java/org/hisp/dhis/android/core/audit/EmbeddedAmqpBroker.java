package org.hisp.dhis.android.core.audit;


import org.apache.qpid.server.Broker;
import org.apache.qpid.server.BrokerOptions;

import java.io.File;
import java.net.URL;

public class EmbeddedAmqpBroker {

    private static final String INITIAL_CONFIG_PATH =
            "messageBroker/messageBrokerConfiguration.json";
    private final Broker broker = new Broker();

    public void start(String port) throws Exception {
        final BrokerOptions brokerOptions = new BrokerOptions();
        brokerOptions.setConfigProperty("qpid.amqp_port", port);


        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource(INITIAL_CONFIG_PATH);
        File file = new File(resource.getPath());

        brokerOptions.setInitialConfigurationLocation(resource.getPath());

        broker.startup(brokerOptions);
    }

    public void stop() {
        broker.shutdown();
    }

}
