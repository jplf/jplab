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

HISTFILE=$HOME/.history.$$~
PS1='\[\033[31;1m\]\273\[\033[0m\] '

. $ENV

PATH=/usr/sbin:/sbin:/bin:/usr/bin:/usr/bin/X11:/usr/local/bin:$HOME/bin
PATH=$PATH:/opt/jdk/bin:$CATALINA_HOME/bin

#______________________________________________________________________________
