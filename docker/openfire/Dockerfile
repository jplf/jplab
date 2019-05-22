#______________________________________________________________________________

# Jean-Paul Le Fèvre - 5 October 2016

# The recipe to build a simple image of the openfire server.
# ___________________________________________________________________________

# It starts from a slackware distrib with Oracle jre
FROM jplf/jpware:jre
MAINTAINER jean-paul.lefevre@cea.fr

# It should be better to choose another user.
ENV OPENFIRE_VERSION=4.0.3 \
    OPENFIRE_HOME=/opt/openfire \
    OPENFIRE_USER=root

# Where the persistent data are stored.
ENV OPENFIRE_DATA_DIR=${OPENFIRE_HOME}/data

# Where java is installed.
ENV JAVA_HOME=/opt/jdk

# The necessary path
ENV PATH=${JAVA_HOME}/bin:${OPENFIRE_HOME}/bin:${PATH}

# Copy a local version of the package. It might be better to wget it.
WORKDIR /opt
COPY openfire_4_0_3.tar.gz /opt
RUN tar xvzf /opt/openfire_4_0_3.tar.gz
RUN rm /opt/openfire_4_0_3.tar.gz


COPY entrypoint.sh /sbin/entrypoint.sh
RUN chmod 755 /sbin/entrypoint.sh
RUN chmod 755 ${OPENFIRE_HOME}/bin/openfirectl

EXPOSE 3478/tcp 3479/tcp 5222/tcp 5223/tcp 5229/tcp \
       7070/tcp 7443/tcp 7777/tcp 9090/tcp 9091/tcp

WORKDIR ${OPENFIRE_HOME}

# What to execute when started.
ENTRYPOINT ["/sbin/entrypoint.sh"]

#______________________________________________________________________________
