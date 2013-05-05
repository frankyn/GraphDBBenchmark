@echo off

mkdir GDB-Distrib
cd GDB-Unified
mvn package
cd target
mv GDB-Unified-1-akka.zip ../../GDB-Distrib
cd ../../GDB-Distrib
7z x -y GDB-Unified-1-akka.zip
rm GDB-Unified-1-akka.zip