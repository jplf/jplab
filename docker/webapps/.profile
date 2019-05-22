#! /bin/sh
#_____________________________________________________________________________
#
#          Environment file - Jean-Paul Le Fèvre
#_____________________________________________________________________________

export ORGROOT=/usr/local
export ENV=$HOME/.bashrc
export DISPLAY=:0
export VISUAL=emacs
export EDITOR=emacs
export HOSTNAME=`hostname`

export CATALINA_HOME=/opt/tomcat
export CMNLIB=$CATALINA_HOME/lib
export XXLDB_HOME=$CATALINA_HOME/webapps/xxldb

HISTFILE=$HOME/.history.$$~
PS1='\[\033[31;1m\]\273\[\033[0m\] '

. $ENV

PATH=/usr/sbin:/sbin:/bin:/usr/bin:/usr/bin/X11:/usr/local/bin:$HOME/bin
PATH=$PATH:/opt/jdk/bin:$CATALINA_HOME/bin

CLASSPATH=$CLASSPATH:$CMNLIB/catalina.jar
CLASSPATH=$CLASSPATH:$CMNLIB/servlet-api.jar
CLASSPATH=$CLASSPATH:$CMNLIB/mysql-connector-java-5.1.25-bin.jar
CLASSPATH=$CLASSPATH:$CMNLIB/tomcat-util.jar
CLASSPATH=$CLASSPATH:$CATALINA_HOME/bin/tomcat-juli.jar

CLASSPATH=$CLASSPATH:$XXLDB_HOME/WEB-INF/lib/jargs-1.0.jar
CLASSPATH=$CLASSPATH:$XXLDB_HOME/WEB-INF/lib/cayenne-2.0.4.jar
CLASSPATH=$CLASSPATH:$XXLDB_HOME/WEB-INF/lib/mail.jar

CLASSPATH=$XXLDB_HOME/WEB-INF/classes:$CLASSPATH
CLASSPATH=$XXLDB_HOME/WEB-INF/etc:$CLASSPATH

export CLASSPATH

#______________________________________________________________________________
