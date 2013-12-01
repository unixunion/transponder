package com.deblox.mods.transponder;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * Created by keghol on 11/30/13.
 *
 * This simple vertical just sends messages to the transponder who should then forward out onto the cluster
 *
 */
public class SimpleVertical extends BusModBase implements Handler<Message<JsonObject>> {

    private String address;
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
        logger.info("subscribing to: " + this.address);
        this.address = getOptionalStringConfig("NODEBUS", this.hostname + ".local");

        logger.info("Handler address to: " + this.address);
        // Start my private periodic handler which publishes heartbeat messages and
        vertx.setPeriodic(25, new PeriodicHandler(this.address));

//        vertx.eventBus().send(this.address, "kwagga");

    }

    @Override
    public void handle(Message<JsonObject> message) {
        // Handle Responses
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

//            JsonObject message = new JsonObject().putString("hello", "world!");
//            message.putString("module", "SimpleVertical"); // append my name as the origin

            //SM m = new SM().newMessage();
            TransponderMessage message = new SM().newMessage();
            message.setMsgBody("ping!");
            logger.info("Created message from module: " + message.getModule());

            logger.info("PeriodicHandler handle method, message generated: " + message.toJsonString());
            logger.info("Sending to address:" + this.address);
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


    private class SM extends TransponderMessage {

//        String module = "somevertical";

//        @Override
//        public String getModule() {
//            return "somevertical";
//        }

        // Override the newMessage method so we can set our module name correctly
        @Override
        public TransponderMessage newMessage() {
            super.newMessage();
            setModule("somevertical");
            return this;
        }

    }

}
