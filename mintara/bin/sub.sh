#!/bin/sh
#______________________________________________________________________________

# Script use to launch the java program Subscriber

# It was downloaded from
# https://github.com/nats-io/java-nats-streaming/releases/tag/2.1.2

# Usage : sub.sh -s $NATS_URL -id the_sub topic

# Jean-Paul Le Fèvre - 22 october 2018
#______________________________________________________________________________

TOP=/home/lefevre/work/git/jplf/mintara

CP=$TOP/build/classes/java/main:$TOP/lib/java-nats-streaming-2.1.0.jar
CP=$CP:$TOP/lib/jnats-2.1.1.jar:$TOP/lib/protobuf-java-3.6.1.jar

java -cp $CP io.nats.streaming.examples.Subscriber $*

#______________________________________________________________________________

