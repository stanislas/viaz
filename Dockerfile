FROM azul/zulu-openjdk:7
MAINTAINER stanislas.nanchen@ergon.ch
ADD target/viaz-0.2-standalone.jar /opt/viaz.jar
CMD java -Dhttp-server-port=3000 -Dzimbra-base-url=http://zimbra.ergon.ch/home/ -Dzimbra-calendar-partial-url=/viaz.xml -Dzimbra-client-options={} -jar /opt/viaz.jar

