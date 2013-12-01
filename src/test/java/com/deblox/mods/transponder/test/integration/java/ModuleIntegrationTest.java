package com.deblox.mods.transponder;/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import com.deblox.mods.transponder.TransponderMessage;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.DecodeException;
import org.vertx.testtools.TestVerticle;

import java.util.zip.DataFormatException;

import static org.vertx.testtools.VertxAssert.*;

/**
 * Example Java integration test that deploys the module that this project builds.
 *
 * Quite often in integration tests you want to deploy the same module for all tests and you don't want tests
 * to start before the module has been deployed.
 *
 * This test demonstrates how to do that.
 */
public class ModuleIntegrationTest extends TestVerticle {

  @Test
  public void testPing() {
    container.logger().info("in testPing()");
    TransponderMessage rmsg = new TransponderMessage().newMessage(TransponderMessage.MsgType.TEST).origin("test");
    container.logger().info("sending message: " +rmsg.toJsonString());
    vertx.eventBus().send("conjoiner.clusterbus", rmsg.toJsonString(), new Handler<Message<String>>() {
      @Override
      public void handle(Message<String> reply) {

          try {
              TransponderMessage msg = new TransponderMessage().messageToObject(reply);
              assertEquals(msg.getMsgType(), TransponderMessage.MsgType.REPLY);
          } catch (DataFormatException e) {
              e.printStackTrace();
          }

        /*
        If we get here, the test is complete
        You must always call `testComplete()` at the end. Remember that testing is *asynchronous* so
        we cannot assume the test is complete by the time the test method has finished executing like
        in standard synchronous tests
        */
        testComplete();
      }
    });
  }

  @Test
  public void testSomethingElse() {
    // Whatever
    testComplete();
  }


  @Override
  public void start() {
    // Make sure we call initialize() - this sets up the assert stuff so assert functionality works correctly
    initialize();
    // Deploy the module - the System property `vertx.modulename` will contain the name of the module so you
    // don't have to hardecode it in your tests
    container.deployModule(System.getProperty("vertx.modulename"), new AsyncResultHandler<String>() {
      @Override
      public void handle(AsyncResult<String> asyncResult) {
      // Deployment is asynchronous and this this handler will be called when it's complete (or failed)
      if (asyncResult.failed()) {
        container.logger().error(asyncResult.cause());
      }
      assertTrue(asyncResult.succeeded());
      assertNotNull("deploymentID should not be null", asyncResult.result());
      // If deployed correctly then start the tests!
      startTests();
      }
    });
  }

//    @Test
//    public void testSimpleVertical() {
//        container.logger().info("Deploying simple vertical");
//        container.deployVerticle("src/main/java/com/deblox/mods/transponder/SimpleVertical.java");
//    }

}
