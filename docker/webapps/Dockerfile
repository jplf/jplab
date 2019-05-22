#_______________________________________________________________________________

# My first webapp dockerized.

# It is based on my tomcat image

# Jean-Paul Le Fèvre - 10 August 2016
# Usage : docker build -t jplf/jpware:myxxl .
#_______________________________________________________________________________

FROM jplf/jpware:tomcat
MAINTAINER Jean-Paul Le Fèvre <jean-paul.lefevre@cea.fr>

ENV JRE_HOME /opt/jdk
ENV CATALINA_HOME /opt/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH

# Check the ip number of the mariadb server and update the config files.

COPY xxldb-realm.jar $CATALINA_HOME/lib/
COPY xxldb.xml $CATALINA_HOME/conf/Catalina/localhost/

# Explode the war file and setup the link to the data directory.
WORKDIR $CATALINA_HOME/webapps
COPY xxldb.war ./
RUN unzip xxldb.war -d xxldb
WORKDIR $CATALINA_HOME/webapps/xxldb
RUN rmdir data
RUN ln -s /var/lib/data/xxldb/data .

# Useful to set a classpath for the xxldb application.
COPY .profile /root

WORKDIR $CATALINA_HOME/logs

EXPOSE 8880
CMD [ "catalina.sh", "run"]

#_______________________________________________________________________________
