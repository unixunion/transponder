package com.deblox.mods.transponder;

/**
 * Created by keghol on 11/28/13.
 *
 * Transponder is responsible for sending ANY communication from other veritcals out to the cluster
 * It should add timestamp and hostname to any message, aswell as sending heartbeat messages and
 * in turn also forwards any message from the cluster on to the local verticals over the modbus.
 *
 */

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;

public class Transponder extends BusModBase implements Handler<Message<JsonObject>> {;

    /*
    * defaults:
    *   NODEBUS address: FQDN.nodebus, address for internal messages
    *   CLUSTERBUS address: conjoiner.clusterbus, address for cluster messages ( transponder only )
    *   HOSTADDRESS address: FQDN.host, address to transponder on THIS hose.
    *
     */

    protected String hostname;
    protected String hostaddress; // the public side interface eg: server01.mydomain.com.host
    protected String nodebus;
    protected String clusterbus;
    protected Integer uniqueIdentifier; // This should be some kind of unique message queue to this host
    protected Logger logger = Logger.getLogger("Transponder");

    @Override
    public void start() {
        logger.setLevel(Level.INFO);
        logger.info("Transponder Starting");
        super.start();

        try {
            this.hostname = getOptionalStringConfig("HOSTNAME", getHostname());
        } catch (UnknownHostException e) {
            logger.warning("Unable to determine hostname, please set LOCAL_ADDRESS");
            e.printStackTrace();
        }

        this.nodebus = getOptionalStringConfig("NODEBUS", this.hostname + ".local");
        this.clusterbus = getOptionalStringConfig("CLUSTERBUS", "conjoiner.clusterbus");
        this.hostaddress = getOptionalStringConfig("HOSTADDRESS", this.hostname + ".host");

        // Start my private periodic handler which publishes heartbeat messages and
        //vertx.setPeriodic(3000, new PeriodicHandler(this.clusterbus));

        // Register myself since I have a handle override with the messagebus
        logger.info("NODEBUS address: " + this.nodebus);
        logger.info("CLUSTERBUS address: " + this.clusterbus);
        logger.info("HOSTADDRESS address: " + this.hostaddress);
        eb.registerHandler(this.nodebus, this);
        eb.registerHandler(this.clusterbus, this);
        eb.registerHandler(this.hostaddress, this);
    }

    // This handle method is used for dealing with messages from both the nodebus and clusterbus
    @Override
    public void handle(Message<JsonObject> message) {
        logger.info("Transponder Handler");
        logger.info("Handler Preparing to deserialize message body: " + message.body());

        logger.info("Converting message back into object");

        try {
            TransponderMessage msg = new TransponderMessage().messageToObject(message);
            if (msg.getModule().equals("")) {
                logger.warning("Message of unknown origin!");
            } else {
                logger.info("Message origin:" + msg.getModule());
            }

            if (msg.getMsgType().equals(TransponderMessage.MsgType.TEST)) {
                logger.info("Responding to TEST message");
                TransponderMessage rmsg = new TransponderMessage().newMessage(TransponderMessage.MsgType.REPLY);
                message.reply(rmsg.toJsonString());
                logger.info("Done responding to message");
            }

            logger.info("Handler got msg: " + msg.toJsonString());

            TransponderMessage reply_message = new TransponderMessage().newMessage(TransponderMessage.MsgType.ACK);
//            reply_message.setOriginal_origin_module(msg.getModule()); // Set the original origin
            message.reply(reply_message.toJsonString());

            // now lets determine the origin of the message and set it in the new message before forwarding onto the cluster


        } catch (DataFormatException e) {
            logger.warning("Error decoding message, is it really json?");
            TransponderMessage reply_message = new TransponderMessage().newMessage(TransponderMessage.MsgType.ACKERROR);
            message.reply(reply_message.toJsonString());
        }



        // Determine if the message was from one of the modules somehow, basically if the message
        // is just some json document not matching the TransponderMessage structure, it should
        // be repacked into the msgBody portion of the new message. a Message MUST contain origin
        // so we know which module it came from!


        logger.info("Handler complete");

    }

    // transponder hostname
    private static String getHostname() throws UnknownHostException {
        java.net.InetAddress addr = InetAddress.getLocalHost();
        String hostname = addr.getHostName();
        return hostname;
    }

    // unique identified for this instance based on the hostname and timestamp
    public String generateIdentifier() throws NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String hex = (new HexBinaryAdapter()).marshal(md.digest(new String(this.hostname + String.valueOf(new Date().getTime())).getBytes()));
//            Integer uniqueIdentifier = new String(this.hostname + String.valueOf(new Date().getTime())).hashCode();
            return hex;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw e;
        }
    }


    // Default messagehandler for any message from local modbus and cluster
//    private void handleMessage(Message<String> message) {
//
//        // Log the Message contents
//        logger.info("Handling Message: " + message.body());
//
//        // Decode the json message body to a new TransponderMessage instance to re-establish methods
//        TransponderMessage imsg = new TransponderMessage().toTransponderMessage(message.body());
//        logger.info("Decoded message: " + imsg.toJsonString());
//
//        if (imsg.getMsgType().equals(TransponderMessage.MsgType.TEST)) {
//            // Respond to test messages with a pong!
//            logger.info("Responding to TEST message");
//            TransponderMessage rimsg = new TransponderMessage().newMessage(TransponderMessage.MsgType.REPLY);
//            message.reply(rimsg.toJsonString());
//        } else {
//            // Determine that the message is not from myself, cause that makes me look crazy.
//            if (imsg.getSrcHost().equals(this.hostname)) {
//                logger.info("Ignoring message from myself...");
//            } else {
//                logger.info("Responding to message");
//                //String response = new Impulse(TransponderMessage.MsgType.ACK).setMsgBody(message.replyAddress()).setHostname(config.getString("HOSTNAME")).toJsonString();
//                TransponderMessage response = new TransponderMessage().newMessage(TransponderMessage.MsgType.REPLY);
//                message.reply(response.toJsonString());
//            }
//
//        }
//
//    }

    private class PeriodicHandler implements Handler<Long> {

        private String broadcast;

        public PeriodicHandler(String broadcast) {
            logger.info("Initialized a periodic handler to topic: " + broadcast);
            this.broadcast = broadcast;
        }

        @Override
        public void handle(Long aLong) {
            logger.info("PeriodicHandler handle called");

            // New message
            TransponderMessage message = new TransponderMessage().newMessage(TransponderMessage.MsgType.HEARTBEAT);
            //JsonObject message = new JsonObject().putString("timestamp", timestamp.toString()).putString("message", "ping");

            logger.info("PeriodicHandler handle method, message generated: " + message.toJsonString());

            vertx.eventBus().send(this.broadcast,
                    message.toJsonString(),
                    new Handler<Message<String>>() {
                        @Override
                        public void handle(Message<String> reply) {
                            logger.info("Response from heartbeat received");
                        }
                    }
            );
        }
    }

}
