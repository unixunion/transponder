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

public class TransponderMessageUnitTest {

    TransponderMessage prepared_mesg;
    String json_string;
    private Logger logger = Logger.getLogger("TransponderMessageUnitTest");

    @Before
    public void setUp() throws Exception {
        // All tests kind of depend on the ability to create a new TransponderMessage from newMessage() method.
        this.json_string = "{\"srcHost\":\"STHMACLT009.local\",\"dstHost\":\"\",\"timestamp\":1385836320954,\"msgType\":\"REPLY\",\"msgBody\":\"{}\",\"module\":\"test\"}";
        this.prepared_mesg = new TransponderMessage().newMessage();
    }

    @Test
    public void testNewMessage() {
       TransponderMessage msg = new TransponderMessage().newMessage();
        // Check the class is right
        assertEquals(new TransponderMessage().getClass(), msg.getClass());
        // Check the newMessage method set the module to "transponder"
        assertEquals("transponder", msg.getModule());
    }

    @Test
    public void testNewMessageType() {
        // Create a new message of type HEARTBEAT
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

    // not sure how to test this since we need an eventbus message
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