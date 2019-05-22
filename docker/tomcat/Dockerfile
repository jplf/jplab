#_______________________________________________________________________________

# My own Tomcat image.

# It is based on Oracle java 8 and Apache tomcat 8.0
# More recent versions of tomcat were found to be buggy.

# Jean-Paul Le Fèvre - 5 August 2016
# Usage : docker build -t jplf/jpware:tomcat .
#_______________________________________________________________________________

FROM jplf/jpware:jre
MAINTAINER Jean-Paul Le Fèvre <jean-paul.lefevre@cea.fr>

ENV TOMCAT tomcat-8.0.36

ENV JRE_HOME /opt/jdk
ENV CATALINA_HOME /opt/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH

WORKDIR /opt
COPY apache-${TOMCAT}.tar.gz /opt

RUN tar xvzf apache-${TOMCAT}.tar.gz
RUN ln -s apache-${TOMCAT} tomcat

WORKDIR $CATALINA_HOME/conf
COPY server.xml .
COPY tomcat-users.xml .

# MySQL driver, standard tag lib
WORKDIR $CATALINA_HOME/lib
COPY *jar ./

WORKDIR /root
RUN mkdir /root/bin
COPY mysql /root/bin
COPY .profile .bashrc ./

EXPOSE 8880
CMD [ "catalina.sh", "run"]

#_______________________________________________________________________________
