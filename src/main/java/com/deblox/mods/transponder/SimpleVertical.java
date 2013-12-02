package com.deblox.mods.transponder;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;

/**
 * Created by keghol on 11/30/13.
 *
 * This simple vertical just sends messages to the transponder who should then forward out onto the cluster
 *
 */
public class SimpleVertical extends BusModBase implements Handler<Message<JsonObject>> {

    private String local_nodebus; // the local_nodebus we subscribe this module to
//    private String cluster_eventbus; // the cluster eventbus we subscribe to ( if any )
    private Logger logger = Logger.getLogger("Transponder");
    private String hostname;


    // transponder hostname
    private static String getHostname() throws UnknownHostException {
        java.net.InetAddress addr = InetAddress.getLocalHost();
        String hostname = addr.getHostName();
        return hostname;
    }

    @Override
    public void start() {
        logger.info("SimpleVertical Starting");

        super.start();

        try {
            this.hostname = getOptionalStringConfig("HOSTNAME", getHostname());
        } catch (UnknownHostException e) {
            logger.warning("Unable to determine hostname, please set LOCAL_ADDRESS");
            e.printStackTrace();
        }

        // Get MODBUS config or set default
        logger.info("subscribing to: " + this.local_nodebus);
        this.local_nodebus = getOptionalStringConfig("NODEBUS", this.hostname + ".local");
//        this.cluster_eventbus = getOptionalStringConfig("CLUSTERBUS", "conjoiner.clusterbus");

        logger.info("Handler local_nodebus to: " + this.local_nodebus);

        // Start my private periodic handler which sends heartbeat messages and deals with replies
        vertx.setPeriodic(5000, new PeriodicHandler(this.local_nodebus));

        // Create a subscription to the nodebus to handle incomming messages
        vertx.eventBus().registerHandler(this.local_nodebus, this);
    }

    @Override
    public void handle(Message<JsonObject> message) {
        // Handle Responses if we subscribe this BusMod implementation directly to something.
        logger.info("Handling event conjoiner.simplevertical");

        // Start by deserializing the message back into a Object
        try {
            TransponderMessage msg = new TransponderMessage().messageToObject(message);
            logger.info("Decoded message: " + msg.toJsonString());

        } catch (DataFormatException e) {
            e.printStackTrace();
            logger.warning("Unable to deserialize this");
        }

    }

    private class PeriodicHandler implements Handler<Long> {

        private String address;

        public PeriodicHandler(String address) {
            logger.info("Initialized a periodic handler to topic: " + address);
            this.address = address;
        }

        @Override
        public void handle(Long aLong) {
            logger.info("PeriodicHandler handle called");

            // Create a new message based on our SM class which extends TransponderMessage
            TransponderMessage message = new SM().newMessage();
            message.setMsgBody("ping!");
            logger.info("Created message from module: " + message.getModule());

            logger.info("PeriodicHandler handle method, message generated: " + message.toJsonString());
            logger.info("Sending to local_nodebus:" + this.address);
            eb.send(this.address,
                    message.toJsonString(),
                    new Handler<Message<String>>() {
                        @Override
                        public void handle(Message<String> reply) {
                            logger.info("Response received: " + reply.body());
                        }
                   }
            );
        }
    }


    // Define a new SM class based off TransponderMessage and override the newMessage method to set the modulename
    // to the name of this module
    private class SM extends TransponderMessage {

        // Override the newMessage method so we can set our module name correctly
        @Override
        public TransponderMessage newMessage() {
            super.newMessage();
            setModule("somevertical");
            return this;
        }

    }

}
