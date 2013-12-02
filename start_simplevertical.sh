#!/bin/bash

. /root/.bashrc

vertx run src/main/java/com/deblox/mods/transponder/SimpleVertical.java -cluster -Djava.util.logging.config.file="/opt/vert.x-2.0.2-final/conf/logging.properties"
