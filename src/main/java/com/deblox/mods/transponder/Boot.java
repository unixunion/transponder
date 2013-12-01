package com.deblox.mods.transponder;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Future;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: keghol
 * Date: 9/24/13
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */

public class Boot extends Verticle{
    JsonObject config;
    private Logger logger = java.util.logging.Logger.getLogger("Transponder");

    public void start(final Future<Void> startedResult) {
        logger.info("Transponder Booting...");

        // load transponder_conf from config file
        config = container.config();
        JsonObject transponderConf = config.getObject("transponder_conf");

        logger.info("Configuration read: " + transponderConf);

        logger.info("Deploying com.deblox.mods.transponder.Transponder...");
        container.deployVerticle("com.deblox.mods.transponder.Transponder", transponderConf ,new AsyncResultHandler<String>() {
            public void handle(AsyncResult<String> deployResult) {
                if (deployResult.succeeded()) {
                    startedResult.setResult(null);
                } else {
                    startedResult.setFailure(deployResult.cause());
                }
            }
        });


//        logger.info("DEPLOYING IO.VERTX MOD AUTH");
//
//        container.deployModule("io.vertx~mod-auth-mgr~2.0.0-final");

    }

}
