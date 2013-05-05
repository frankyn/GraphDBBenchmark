-= GDB: Graph Database Benchmark =-

GDB is a distributed graph database benchmarking framework.
GDB's purpose is to test and compare different Blueprints-compliant graph databases. This tool can be used to simulate real graph database work loads with any number of concurrent clients performing any type of operation on any type of graph.

Requirements
------------
-Java JDK 7
-Python 2.7.x
-Git
-Apache Maven
-7zip

Installation
============
Cloning the project
-------------------
To clone GDB sources from Github, open a command prompt, go to the folder where
you want to clone the project and type:
git clone https://github.com/Morro/GraphDBBenchmark.git

Building the project
--------------------
To build GDB on a Linux host, run build.sh (ie. build.bat on a Windows host). This will create a GDB-Distrib directory containing executable files.
Copy this directory to as many machines as required.

Starting GDB
------------
On each machine, you will have to modify the file GDB-Unified/config/application.conf. This file is the AKKA configuration file used to configure all the actors of the system.
On the server machine, change the IP address in the SSys section of the file with the network address that must be used by the server. The same configuration applies for the master client (MCSys section) and the slave clients (SCSys section) actors.
To start the server on a Linux host, run GDB-Unified/startserver.sh (ie. startserver.bat on a Windows host).
To start the master client on a Linux host, run GDB-Unified/startmc.sh (ie. startmc.bat on a Windows host).
To start a slave client on a Linux host, run GDB-Unified/startsc.sh (ie. startsc.bat on a Windows host).
After having started the server, the master client and at least one slave client, the user is allowed to start running the benchmark by opening a command prompt, going to the GDB-Distrib directory and typing:

java -jar client . jar serverAdd serverPort masterClientAddress masterClientPort slaveClient1Address slaveClient1Port ... slaveClientXAddress slaveClientXPort

This will start the benchmark runner locally and all the other actors. Then, the benchmark runner will receive instructions from the benchmark defined by the user.