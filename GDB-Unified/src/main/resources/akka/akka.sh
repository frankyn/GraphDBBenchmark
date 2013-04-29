#!/bin/bash

AKKA_HOME="$(cd "$(cd "$(dirname "$0")"; pwd -P)"/..; pwd)"

[ -n "$JAVA_OPTS" ] || JAVA_OPTS="-Xms512M -Xmx5G -Xss1M -XX:MaxPermSize=256M -XX:+UseParallelGC"

[ -n "$AKKA_CLASSPATH" ] || AKKA_CLASSPATH="$AKKA_HOME/lib/scala-library-2.9.1-1.jar:$AKKA_HOME/lib/*:$AKKA_HOME/config"

[ -n "$CLASSPATH" ] || CLASSPATH="$CLASSPATH:$AKKA_HOME/resources/*"

java $JAVA_OPTS -javaagent:$AKKA_HOME/lib/jamm-0.2.5.jar -cp "$AKKA_CLASSPATH" -Dakka.home="$AKKA_HOME" akka.kernel.Main $1
