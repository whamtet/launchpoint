#!/bin/bash
java -Dclojure.server.repl="{:port 5551 :accept clojure.core.server/repl}" -jar target/launchpoint-standalone.jar
