package com.deblox.mods.transponder.test.unit;

import com.deblox.mods.transponder.TransponderMessage;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.json.JsonObject;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * Created by keghol on 11/29/13.
 */


public class TransponderMessageTest {

    TransponderMessage prepared_mesg;
    String json_string;
    private Logger logger = Logger.getLogger("Transponder");

    @Before
    public void setUp() throws Exception {
        // All tests kind of depend on the ability to create a new TransponderMessage from newMessage() method.
        this.json_string = "{\"srcHost\":\"STHMACLT009.local\",\"dstHost\":\"\",\"timestamp\":1385836320954,\"msgType\":\"REPLY\",\"msgBody\":\"\",\"origin\":\"\"}";
        this.prepared_mesg = new TransponderMessage().newMessage();
    }

    @Test
    public void testNewMessage() {
       TransponderMessage msg = new TransponderMessage().newMessage();
        assertEquals(new TransponderMessage().getClass(), msg.getClass());
    }

    @Test
    public void testNewMessageType() {
        TransponderMessage msg = new TransponderMessage().newMessage(TransponderMessage.MsgType.HEARTBEAT);
        assertEquals(TransponderMessage.MsgType.HEARTBEAT, msg.getMsgType());
    }

    @Test
    public void testToJson() {
        // Create a new TransponderMessage from json String
        TransponderMessage nm = new TransponderMessage().jsonStringToObject(json_string);
        // Test the toJsonString method on the TransponderMessage instance we created
        assertEquals(json_string, nm.toJsonString());
    }

//    @Test
//    public void testGetMsgType() {
//        assertEquals(prepared_mesg.getMsgType(), TransponderMessage.MsgType.UNKNOWN);
//    }
//
//    @Test
//    public void testSetSrcHost() {
//        // also indirectly tests getSrcHost
//        TransponderMessage msg = new TransponderMessage().newMessage();
//        msg.setSrcHost("Foo");
//        assertEquals("Foo", msg.getSrcHost());
//    }
//
//    @Test
//    public void testSetTimestamp() {
//        // Tests getter also
//        long ts = new Long(0);
//        prepared_mesg.setTimestamp(ts);
//        assertEquals(ts, prepared_mesg.getTimestamp());
//
//    }

//    public void testSetMsgType() {
//        prepared_mesg.setMsgType(TransponderMessage.MsgType.ACK);
//
//    }
//
//    public void testGetSrcHost() {
//
//    }
//
//    public void testGetMsgBody() {
//
//    }
//
//    public void testToTransponderMessage() {
//
//    }

    public void testJsonToObject() {


    }


    public void testMessageToObject() {

    }

    @Test
    public void testJsonStringToObject() {
        TransponderMessage tm = new TransponderMessage().jsonStringToObject(json_string);
        assertEquals(new TransponderMessage().getClass(), tm.getClass());
    }

    @Test
    public void testGenericJsonStringToObject() {
        String js = "{\"msgBody\":\"bar\"}";
        TransponderMessage tm = new TransponderMessage().jsonStringToObject(js);
        assertEquals("bar", tm.getMsgBody());
    }

    @Test
    public void testGenericNestedJsonStringToObject() {
        JsonObject jo = new JsonObject().putString("foo", "bar");
        JsonObject fo = new JsonObject().putString("msgBody", jo.toString());
        logger.info(fo.toString());
        String js = fo.toString();
        TransponderMessage tm = new TransponderMessage().jsonStringToObject(js);
        assertEquals(jo.toString(), tm.getMsgBody());
    }

}