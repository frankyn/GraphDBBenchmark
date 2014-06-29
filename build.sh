@echo off

mkdir GDB-Distrib
cd GDB-Unified
cd "external deps"
sh install_deps.sh
cd ..
mvn package
cd target
mv GDB-Unified-1-akka.zip ../../GDB-Distrib
cd ../../GDB-Distrib
7z x -y GDB-Unified-1-akka.zip
rm GDB-Unified-1-akka.zip
mv ../GDB-Unified/target/GDB-Unified-1-jar-with-dependencies.jar GDB-Unified-1/client.jar
mv GDB-Unified-1/* ./