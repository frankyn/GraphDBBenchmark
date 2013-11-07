@echo off

md GDB-Distrib
cd GDB-Unified
cd "external deps"
CALL install_deps.bat
cd ..
call mvn package
cd target
move GDB-Unified-1-akka.zip ..\..\GDB-Distrib
cd ..\..\GDB-Distrib
7z x -y GDB-Unified-1-akka.zip
del GDB-Unified-1-akka.zip
move ..\GDB-Unified\target\GDB-Unified-1-jar-with-dependencies.jar GDB-Unified-1\client.jar
robocopy /move /e GDB-Unified-1 .