#!/bin/sh
#vertx runzip build/libs/transponder-1.0.0-final.zip -cluster  -cluster-port 25501 -cluster-host 127.0.0.1 -Djava.util.logging.config.file="/opt/vert.x-2.0.1-final/conf/logging.properties"
. /root/.bashrc

cd /opt/transponder

vertx run src/main/java/com/deblox/mods/transponder/Transponder.java -cluster -Djava.util.logging.config.file="/opt/vert.x-2.0.2-final/conf/logging.properties"
