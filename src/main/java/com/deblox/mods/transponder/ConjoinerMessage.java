package com.deblox.mods.transponder;

/**
 * Created by keghol on 12/2/13.
 *
 * Not used yet, should replace the existing TransponderMessage functionality allowing easier extends to verticals
 *
 */
public abstract class ConjoinerMessage {
    public enum MsgType {
        UNKNOWN, HEARTBEAT, UPDATE_REQUEST, UPDATE_RESPONSE, TEST, ACK, REPLY, VOTE, ACKERROR, ERROR;
    }

    public abstract String getHello();

    public String getHello2() {
        return "hello2";
    }

}