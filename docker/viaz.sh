#!/usr/bin/env bash

JAVA=/usr/bin/java

$JAVA -Dhttp-server-port=3000 -Dzimbra-base-url=http://zimbra.ergon.ch/home/ -Dzimbra-calendar-partial-url=/viaz.xml -Dzimbra-client-options={} -jar /opt/viaz.jar

