package com.deblox.mods.transponder.test.unit;

import com.deblox.mods.transponder.Transponder;
import org.junit.Before;
import org.junit.Test;

import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/*
 * @author <a href="http://kegans.com">Kegan Holtzhausen</a>
 */

public class TransponderUnitTest {

    private Logger logger = Logger.getLogger("UnitTest");
    Transponder transponder;

    @Before
    public void setUp() throws Exception {
        logger.info("Setting up TransponderUnitTest");
        transponder = new Transponder();
    }

    @Test
    public void testVerticle() {
        assertNotNull(transponder);
    }

    @Test
    public void testUniqueId() {
        try {
            String id = transponder.generateIdentifier();
            logger.info("ID: " + id);
            assertNotNull(id);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


}


