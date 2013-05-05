@echo off

md GDB-Distrib
cd GDB-Unified
call mvn package
cd target
move GDB-Unified-1-akka.zip ..\..\GDB-Distrib
cd ..\..\GDB-Distrib
7z x -y GDB-Unified-1-akka.zip
del GDB-Unified-1-akka.zip