**Mintara** is the place where I store my Nats test code.

### Overview

The [Nats](https://nats.io/) framework is a messaging system
providing programs, libraries, tools to allow exchanges of messages between
applications.

[Mintara](https://en.wikipedia.org/wiki/Tarabya_of_Ava) is one of the
first [Nats](https://en.wikipedia.org/wiki/Nat_(spirit)).

The first thing to do is to  install the system, e.g. by pulling the docker image: `docker pull nats-streaming:0.11.2`

Launch the server daemon: `docker run -d -p 4222:4222 --name mintara nats-streaming:0.11.2 -cid svom-cluster -a localhost`


### First steps

Example programs are proposed on this [tutorial](https://github.com/nats-io/java-nats/blob/master/src/examples/java/io/nats/examples/examples.md).

The java client [documentation](https://github.com/nats-io/java-nats-streaming) and
the [javadoc](http://javadoc.io/doc/io.nats/java-nats-streaming/2.1.1) are available online.

It is also worth reading the source code of these [examples](https://github.com/nats-io/java-nats-streaming/releases/tag/2.1.2).

To learn how to use the library 2 first programs were written: `org.svom.mintara.worker.Greeter`,
[the producer](src/main/java/org/svom/mintara/worker/Greeter.java) and
`org.svom.mintara.worker.Patron`,
[the consumer](src/main/java/org/svom/mintara/worker/Patron.java).

This demo is built using `gradle`: here is the [build.gradle](build.gradle).

Take care : some parameters are hardcoded !


### The demo

This demo application is meant to prepare the Svom ground segment computing framework.

The model used follows the *publish and subscribe* paradigm.

* [`Greeter`](src/main/java/org/svom/mintara/worker/Greeter.java) a simple java program used to send messages. It is a publisher. Once launched it enters a loop waiting for user's input.
If the character '*x*' or '*q*' is given the loop is stopped and the program exits. If the string '*close*' is given running consumers
are cleanly closed.

* [`Patron`](src/main/java/org/svom/mintara/worker/Patron.java) is simple java program used to receive messages. It is a consumer.


Build the programs : `gradle classes`

Set the `CLASSPATH` with:
````
 mintara/build/classes/main
 java-nats-streaming-2.1.0.jar
 jnats-2.1.1.jar
 protobuf-java-3.6.1.jar

````
or use the scripts [`greeter.sh`](https://drf-gitlab.cea.fr/lefevre/jplf/blob/master/mintara/bin/greeter.sh)
and [`patron.sh`](https://drf-gitlab.cea.fr/lefevre/jplf/blob/master/mintara/bin/patron.sh)

### The server

It is started in the Svom docker swarm.

The configuration is created by :

````
docker secret create -l content="Svom Messaging configuration" nats.conf nats-server.yml
docker secret inspect nats.conf

````

<table>
<tr>
<td><b>proxy path</b></td><td><b>service</b></td><td><b>port</b></td><td><b>description</b></td>
</tr>
<tr>
<td>n/a</td><td>nats server</td><td>5522</td><td>nats streaming server</td>
</tr><tr>
<td>msg</td><td>nats monitoring</td><td>8222</td><td>nats server status</td>
</tr>
<tr>
<td>visu</td><td>container visualizer</td><td>8080</td><td>swarm visualizer</td>
</tr>
</table>

#### CI/CD

First, edit the `gitlab-ci.yml` file at the root of the project. See the [documentation](https://docs.gitlab.com/ee/ci/yaml/).

Then register a runner :

````
docker run --rm -it --name mintara-runner \
  -v /var/lib/docker/gitlab-runner/config:/etc/gitlab-runner \
  -v /etc/docker/certs.d/gitlab-runner:/etc/gitlab-runner/certs \
  gitlab/gitlab-runner:alpine register
  
````
Parameters can be found in the Settings section. The tag `jplf` is specified. The executor is taken as docker alpine:latest.

The resulting configuration is kept in `/var/lib/docker/gitlab-runner/config/config.toml`

To see the status of the registered runners run :
````
docker run --rm -it --name jp-runner \
-v /var/lib/docker/gitlab-runner/config:/etc/gitlab-runner \
-v /etc/docker/certs.d/gitlab-runner:/etc/gitlab-runner/certs \
  gitlab/gitlab-runner:alpine list
````


Finally run the runner :

````
docker run -d --name mintara-runner --restart always \
  -v /var/lib/docker/gitlab-runner/config:/etc/gitlab-runner \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /etc/docker/certs.d/gitlab-runner:/etc/gitlab-runner/certs \
   gitlab/gitlab-runner:alpine
````

and check the pipeline status.
