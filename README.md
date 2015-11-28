# viaz

A Clojure webapp designed to produce viaz_add entries from a zimbra calendar

## Usage

java -Dhttp-server-port=3000 -Dzimbra-base-url=http://zimbra.ergon.ch/home/ -Dzimbra-calendar-partial-url=/viaz.xml -Dzimbra-client-options={} -jar target/viaz-{version}-standalone.jar

## Docker (on jamal)

docker build -t jamal.ergon.ch:5000/viaz:0.2 .

# kill previous builds.
docker run -p=8180:3000 -d jamal.ergon.ch:5000/viaz:0.2

## License

Copyright © 2012-2015 Stanislas Nanchen

Distributed under the Eclipse Public License, the same as Clojure.
