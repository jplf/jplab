**Docker** is the place where I store my docker code.

### Installation

Install [docker](https://docs.docker.com/linux/) on a 64 bits linux box.
(Docker is not available for 32 bits system) Follow the instructions given on the [web page](https://docs.docker.com/linux/step_one/).

Current user must belong to the `docker` group.

Make sure that `docker` is running; check the output of : `/etc/rc.d/rc.docker status`

To check the installation run : `docker info`

To list locally installed images run : `docker images` then try `docker inspect some_id`

To get rid of an image : `docker rmi some_id`

To clean up exited docker processes:
`docker ps -a | grep Exit | awk '{print $1}' | xargs docker rm`
<br><br>
[Reference doc](https://docs.docker.com/engine/reference/commandline/cli) on the command line interface.
<br><br>
Try some available containers with the command [docker run](https://docs.docker.com/engine/reference/run/).
A lot of containers can be found in the [hub](https://hub.docker.com/explore/).

For instance try :

`docker run -i -t --name=slack vbatts/slackware:current /bin/sh`

`docker ps`

`docker cp rootenv condescending_noyce:/root/tmp`

`docker commit -m "Root environment prepared" condescending_noyce myslack:current`

`slackpkg update`

`docker stop naughty_engelbart`

### Building an image

[Dockerfile reference] (https://docs.docker.com/engine/reference/builder/)

**A tiny slackware with my environment**

`emacs` [Dockerfile] (https://drf-gitlab.cea.fr/lefevre/jplf/blob/master/docker/jpware/Dockerfile)

`docker build -t jplf/jpware .`

`docker inspect jplf/jpware`

`docker run -i -t jplf/jpware`

`docker stop sick_jones`

`docker rm sick_jones`

`docker rmi 99a1754bccbe`
<br><br>

**A nodejs server with angularjs, taken from the sandbox**

`docker build -t jplf/jpnode:try .`

`docker run -p 8100:8000 jplf/jpnode:try`
<br><br>
**Transporting images**

`docker save -o slackware.tar vbatts/slackware`

`docker load -i slackware.tar`
<br><br>
**Containerization of an existing application**

`cd app`

`copy Dockerfile app/`

`copy .dockerignore app/`

`emacs Dockerfile &`

`docker build -t jplf/app:v1.0 .`

`docker inspect jplf/app:v1.0`

`docker run -p 8000:8000 jplf/app:v1.0`
<br><br>
**MariaDB in a container**

[Documentation](https://hub.docker.com/_/mariadb/) about this container is available.

Stop any running mysqld and any zombie container then
start the image, mounting a local directory to be visible inside the container:

`docker ps -a`

`docker run -d --name mariadb -e MYSQL_ROOT_PASSWORD=dbadmin_mot2passe -v /xtra/data/mysql:/var/lib/mysql mariadb:latest`

`docker logs mariadb`

`docker exec -it mariadb bash`

Inside the container try :

`mysql -u lefevre -p`

Then run any standard mysql commands.

From the host machine, first find out which IP number to use :

`docker inspect mariadb`

`mysql -u lefevre -p -h 172.17.0.2 xmm_xxl_db`

<br><br>
**MongoDB in a container**

Start the image, mounting a local directory to be visible inside the container:

`docker run -d --name mymongo -v $MY_GIT/vhf-hosting/mongodb:/var/db/hosting mongo:3.2.5`

Publish the server port:

`docker run -d --name mymongo -p 27017:27017 -v /xtra/data/docker/mongodb:/data/db mongo:3.2.5`

Configure this instance. Run the client program `mongo` interactively (`-it`) on the running image `mongodb` and open the database `admin`

`docker exec -it mongodb mongo admin`

`> db.runCommand({buildInfo: 1});`

`> db.createUser({user: "lefevre", roles: ["clusterAdmin","userAdminAnyDatabase","readAnyDatabase"]});`

Restore the content of a database found on the locahost into the containerized database.

`docker exec -it mymongo mongorestore -u lefevre -p $MY_PW --drop --db test /var/db/hosting/dump/test`

`mongorestore -u lefevre -p $MY_PW --drop --db test dump/test`

`mongo -u lefevre -p $MY_PW test`

`> show collections;`

More documentation in this [wiki page] (https://drf-gitlab.cea.fr/lefevre/jplf/wikis/mongodb)
<br><br>
**Containerization and linking**

`docker build -t jplf/vhf-hosting:v1.3 .`

`docker run -d --name mymongo -v /xtra/data/docker/mongodb:/data/db mongo:3.2`

`mongorestore -u svompal -p $HOSTINGDB_PW --drop --db hostingdb dump/hostingdb`

<br>
Link vh-hosting with mongodb.

Find out mongodb ip number :
````
docker inspect mymongo | fgrep IPAddress
export DB_PORT_27017_TCP_ADDR=nnn.nnn.nnn.nnn
echo $DB_PORT_27017_TCP_ADDR
````


`docker run -d -p 8000:8000 --name hosting_webapp -e HOSTINGDB_PW -e NODE_PORT=8000 -e MONGODB_HOST=db --link mymongo:db jplf/vhf-hosting:v1.3`

`docker logs hosting_webapp`

<br>
Display the list of available images:

`docker images`
<br><br>
**Tomcat in a container, episode 1**

[Documentation](https://hub.docker.com/_/tomcat/) about this container is available.

Copy a configuration file from the host into the container.

Make sure that these files are correct : copy them from a working local installation.

Tomcat versions 9 and 8.5 seem to be buggy : md5 authentication does not work.
Take care also of the java (javac, jre, jvm) versions.

`docker run -it -p 8888:8080 --name=mycat tomcat:8.0 bash`

`docker cp server.xml mycat:/usr/local/tomcat/conf/server.xml`

`docker cp tomcat-users.xml  mycat:/usr/local/tomcat/conf/tomcat-users.xml`

`/usr/local/tomcat/bin/startup.sh`

`docker commit -m "Configuration fixed for me" mycat tomcat:8.0-jp`

If everything is fine then start :

`docker run -d --rm -p 8888:8080 --name=mycat tomcat:8.0-jp`

Browse to the url `http://localhost:8888`
<br><br>
**Java in a container**

First images found in the official docker hub belong to the OpenJDK family. It may be a source of difficulties
when it comes to build new images with existing jar files compiled with the Oracle compiler.
<br>
As an alternative we will generate our java-equipped image:
first download something like `jre-8u102-linux-x64.tar.gz` from Oracle. Then copy the tarball into the container:

`docker cp jre-8u102-linux-x64.tar.gz myslack:/root/tmp`

Inside the container do:

`cd /opt; tar xvzf /root/tmp/jre-8u102-linux-x64.tar.gz; ln -s jre1.8.0_102 jdk`

`docker commit -m "JRE installed" myslack myslack:jre`
<br><br>
**Tomcat in a container, episode 2**

Starting from `myslack:jre` and `tomcat-8.0.36`

To glance at the base image run :

`docker run -it --rm --name=myslack jplf/jpware:jre`

Check and update if needed the [Dockerfile] (https://drf-gitlab.cea.fr/lefevre/jplf/blob/master/docker/tomcat/Dockerfile)

`docker build -t jplf/jpware:tomcat .`

`docker run -d -p 8888:8080 --name=mycat jplf/jpware:tomcat`

More documentation in this [wiki page] (https://drf-gitlab.cea.fr/lefevre/jplf/wikis/webapp)

<br></br>
**Openfire in a container**

The starting point was this [github package](https://github.com/sameersbn/docker-openfire).

It was modified in order to base the image on the last version of [Openfire](https://www.igniterealtime.org/projects/openfire/) and to use the Oracle version of java.

It was painful to fix all the details of the [`Dockerfile`](https://drf-gitlab.cea.fr/lefevre/jplf/blob/master/docker/openfire/Dockerfile)
and of the [`entrypoint`](https://drf-gitlab.cea.fr/lefevre/jplf/blob/master/docker/openfire/entrypoint.sh). It everything is ok the configuration is initialized
from the distributed package and stored locally the first time the image is run. Then modifications are made persistent on a local directory mounted as a volume when
the server is updated.

`docker run -dt --name openfire --publish 9090:9090 --publish 5222:5222 --volume /var/docker/openfire:/opt/openfire/data jplf/openfire`



---