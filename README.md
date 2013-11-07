GDB: Graph Database Benchmark
=============================
GDB is a distributed graph database benchmarking framework.
GDB's purpose is to test and compare different Blueprints-compliant graph databases. This tool can be used to simulate real graph database work loads with any number of concurrent clients performing any type of operation on any type of graph.

Requirements
------------
-	Java JDK 7
-	Python 2.7.x
-	Git
-	Apache Maven
-	7zip

Installation
------------
### Cloning the project ###
To clone GDB sources from Github, open a command prompt, go to the folder where
you want to clone the project and type:
*git clone https://github.com/Morro/GraphDBBenchmark.git*

### Building GDB ###
To build GDB on a Linux host, run build.sh (resp. build.bat on a Windows host). This will create a GDB-Distrib directory containing executable files.
Copy this directory to as many machines as required.

### Starting GDB actors ###
On each machine, you will have to modify the file GDB-Distrib/config/application.conf. This file is the AKKA configuration file used to configure all the actors of the system.
On the server machine, change the IP address (and optionally the port number) in the SSys section of the file with the network address that must be used by the server. 
The same configuration applies for the master client (MCSys section) and the slave clients (SCSys section) actors.

To start the server on a Linux host, run GDB-Distrib/startserver.sh (resp. startserver.bat on a Windows host).  
To start the master client on a Linux host, run GDB-Distrib/startmc.sh (resp. startmc.bat on a Windows host).  
To start a slave client on a Linux host, run GDB-Distrib/startsc.sh (resp. startsc.bat on a Windows host).  

After having started the server, the master client and at least one slave client, the user is allowed to start running a benchmark.

### Defining and starting your benchmark ###
#### Source mode ####
In order to define a custom benchmark, start by modifying the file GDB-Distrib/src/main/java/com/silvertower/app/bench/main/Benchmark.java.  
There are two methods that can be modifed:  
-	The method Benchmark#addInitializers defines which graph databases will be evaluated during the benchmark  
-	The method Benchmark#benchmark specifies which workloads will be executed on each graph database

When your benchmark is defined, rebuild GDB. Then, to start running your benchmark, open a command prompt, go to the GDB-Distrib directory and type:  
*java -jar client.jar commandline serverAdd serverPort masterClientAddress masterClientPort slaveClient1Address slaveClient1Port ... slaveClientXAddress slaveClientXPort*

This will start the benchmark runner locally and all the other actors. Then, the benchmark runner will receive instructions from the benchmark defined by the user.

#### GUI mode ####
To start GDB's visual interface open a command prompt, go to the GDB-Distrib directory and type:  
*java -jar client.jar gui*

### Results and visualizations ###
Logs are generated during the benchmark for further analysis and comparisons (a log file GDB-Distrib\log\log.txt is created at benchmark startup). One can also define results visualizations in source mode. The resulting figures are output in GDB-Distrib\plots.