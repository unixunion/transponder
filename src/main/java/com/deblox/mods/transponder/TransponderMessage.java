package com.deblox.mods.transponder;

import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import java.util.logging.Logger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.zip.DataFormatException;

/**
 * Created by keghol on 11/29/13.
 */

public class TransponderMessage {

    public enum MsgType {
    UNKNOWN, HEARTBEAT, UPDATE_REQUEST, UPDATE_RESPONSE, TEST, ACK, REPLY, VOTE, ACKERROR, ERROR;
}
     private String module;
     private String srcHost = "";
//     private String original_origin_module = "";
     private String dstHost = "";
     private long timestamp = new Date().getTime();
     private MsgType msgType = MsgType.UNKNOWN; // HEARTBEAT, UPDATE_REQUEST, ...
     private String msgBody = "";
     private Logger logger = Logger.getLogger("TransponderMessage");

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
//        logger.info("Setting module to: " + module);
//        if ( this.module != "" && this.getOriginal_origin_module() == "") {
//            logger.info("setting original_origin_module to: " + this.getModule());
//            this.setOriginal_origin_module(this.getModule());
//        }
        this.module = module;
    }

    public TransponderMessage origin(String origin) {
        this.setModule(origin);
        return this;
    }

//    public String getOriginal_origin_module() {
//        return original_origin_module;
//    }
//
//    public void setOriginal_origin_module(String original_origin_module) {
//        this.original_origin_module = original_origin_module;
//    }

    public TransponderMessage() {
        // nothing should be in here, else messes with the deserializing
    }

    public TransponderMessage newMessage() {
        logger.info("Creating new TransponderMessage");
        InetAddress addr = null;

        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        TransponderMessage msg = new TransponderMessage();
        msg.setSrcHost(addr.getHostName());
        msg.setTimestamp(new Date().getTime());
        msg.setModule("transponder"); // newMessage here sets transponder
        return msg;
    }

    public TransponderMessage newMessage(MsgType msgType) {
        TransponderMessage msg = new TransponderMessage().newMessage();
        msg.setMsgType(msgType);
        return msg;
    }

    private JsonObject objectToJson(TransponderMessage o) {
        // Takes a TransponderMessage and returns a JsonObject of it and it fields.
        logger.info("objectToJson deserializing object to json: " + o.toString());

        JsonObject json = new JsonObject()
                .putString("srcHost", this.srcHost)
                .putString("dstHost", this.dstHost)
                .putNumber("timestamp", this.timestamp)
                .putString("msgType", this.msgType.toString())
                .putString("msgBody", this.msgBody)
                .putString("module", getModule());
//                .putString("original_origin_module", this.original_origin_module);
        return json;

    }

    public String getSrcHost() {
        return srcHost;
    }

    public void setSrcHost(String srcHost) {
        this.srcHost = srcHost;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setDstHost(String dstHost) {
        this.dstHost = dstHost;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public static MsgType getMsgTypeFromString(String t){
        MsgType[] msgTypes = MsgType.values();

        for (MsgType msgType : msgTypes) {
            if (msgType.toString().equals(t)) {
                return msgType;
            }
        }
        return null;

    }

    private TransponderMessage jsonToObject(JsonObject json) {
        logger.info("Converting message: " + json + " to TransponderMessage");
        TransponderMessage nm = new TransponderMessage();
        nm.setSrcHost(json.getString("srcHost", ""));
        json.removeField("srcHost");
        nm.setDstHost(json.getString("dstHost", ""));
        json.removeField("dstHost");
        nm.setTimestamp(json.getLong("timestamp", new Date().getTime()));
        json.removeField("timestamp");
        nm.setMsgType(getMsgTypeFromString(json.getString("msgType", "UNKNOWN")));
        json.removeField("msgType");
        nm.setMsgBody(json.getString("msgBody", ""));
        json.removeField("msgBody");
        nm.setModule(json.getString("module", ""));
        json.removeField("module");
//        nm.setOriginal_origin_module(json.getString("original_origin_module", ""));
//        json.removeField("original_origin_module");
        logger.info("Left overs: " + json.toString());

        if (nm.getMsgBody() == "")  {
            logger.info("Since msgBody is empty, plopping leftovers into it");
            nm.setMsgBody(json.toString());
        }

//        if (nm.getOriginal_origin_module() == "") {
//            logger.info("Setting original module to: " + nm.getModule());
//            nm.setOriginal_origin_module(nm.getModule());
//        }

        return nm;
    }

    // Convert json string back into TransponderMessage instance
    public TransponderMessage jsonStringToObject(String json) {
        logger.info("jsonStringToObject String");
        logger.info("jsonStringToObject");
        JsonObject jn = new JsonObject(json);
        return jsonToObject(jn);
    }

    // Convert message instance back into TransponderMessage instance
    public TransponderMessage messageToObject(Message message) throws DataFormatException {
        try {
            logger.info("messageToObject Message");
            logger.info("messageToObject: " + message.body());
            JsonObject jn = new JsonObject(message.body().toString());
            logger.info("Created json object: " + jn.toString());
            return jsonToObject(jn);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataFormatException("Message is not JsonObject");
        }
    }

    public String toJsonString() {
        logger.info("toJsonString");
        return objectToJson(this).toString();
    }
}
