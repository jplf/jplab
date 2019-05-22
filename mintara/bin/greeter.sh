#!/bin/sh
#______________________________________________________________________________

# Script use to launch the java program Greeter
# It was written by me.
# Jean-Paul Le Fèvre - 22 october 2018
#______________________________________________________________________________

# Set the variable NATS_URL to get authenticated
# export NATS_URL=nats://svom:mot2passe@server_name:4222

TOP=/home/lefevre/work/git/jplf/mintara
LIB=$TOP/lib

CP=$TOP/build/classes/java/main:$LIB/java-nats-streaming-2.1.0.jar
CP=$CP:$LIB/jnats-2.1.1.jar:$LIB/protobuf-java-3.6.1.jar
CP=$CP:$LIB/google-options-1.0.0.jar:$LIB/guava-27.0.1-jre.jar

java -cp $CP org.svom.mintara.worker.Greeter $*

#______________________________________________________________________________

