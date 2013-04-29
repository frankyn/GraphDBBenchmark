@echo off
set AKKA_HOME=%~dp0..
set JAVA_OPTS=-Xms512M -Xmx5G -Xss1M -XX:MaxPermSize=256M -XX:+UseParallelGC
set AKKA_CLASSPATH=%AKKA_HOME%\lib\scala-library-2.9.1-1.jar;%AKKA_HOME%\lib\*;%AKKA_HOME%\config
set CLASSPATH=%CLASSPATH%;%AKKA_HOME%\resources\*

java -javaagent:%AKKA_HOME%/lib/jamm-0.2.5.jar %JAVA_OPTS% -cp "%AKKA_CLASSPATH%" -Dakka.home="%AKKA_HOME%" akka.kernel.Main %1
