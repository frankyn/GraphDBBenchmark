#!/bin/bash

AKKA_HOME="$(cd "$(cd "$(dirname "$0")"; pwd -P)"/..; pwd)"

[ -n "$JAVA_OPTS" ] || JAVA_OPTS="-Xms1536M -Xmx1536M -Xss1M -XX:MaxPermSize=256M -XX:+UseParallelGC"

[ -n "$AKKA_CLASSPATH" ] || AKKA_CLASSPATH="$AKKA_HOME/lib/scala-library-2.9.1-1.jar:$AKKA_HOME/lib/*:$AKKA_HOME/config"

java $JAVA_OPTS -cp "$AKKA_CLASSPATH" -Dakka.home="$AKKA_HOME" akka.kernel.Main $1
