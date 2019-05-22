**Bunny** is the place where I store my RabbitMQ test code.

### Overview

The [RabbitMQ](https://www.rabbitmq.com/) framework is a messaging system
providing programs, libraries, tools to allow exchanges of messages between
applications.

First install the system, e.g. by pulling a docker image: `docker pull rabbitmq`

It is best to get the image with the *management* tag to take advantage of the management plugin.

The local host has the alias name *hurukan*. To make things simpler the network of the container 
is declared as being the host's one.

Launch the daemon: `docker run -d --network=host -h hurukan --name bunny rabbitmq:latest`

Use the `management` tag instead to see what it going on on the server with a browser pointing to `hurukan:15672`

### First steps

The official *hello word* programs are described in these [tutorials](https://www.rabbitmq.com/getstarted.html) on the RabbitMQ site.

The java client [API](https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/) is online.

To learn how to use the library 2 first programs were written: `org.svom.bunny.worker.Greeter`,
[the producer](src/main/java/org/svom/bunny/worker/Greeter.java) and
`org.svom.bunny.worker.Patron`,
[the consumer](src/main/java/org/svom/bunny/worker/Patron.java).

This demo is built using `gradle`: here is the [build.gradle](build.gradle).

### The demo

This demo application is meant to prepare the Svom ground segment computing framework.

#### Use case

Inside the Svom FSC a set of data processing pipelines are expected to run  and to collaborate
to generate a collection of scientific products on a regular basis. Most of the time these pipelines 
take datasets as input, carry out various calculations and eventually generate results stored in output files.
They have to work together which means that outputs of some to them are inputs of other ones, and they have
to synchronize their activities.

#### The processor program

It is a program simulating what will be a pipeline in the FSC. It reads some files in input and generates an output file.
It spends some time in a loop doing something. Naturally this program does not make anything useful, it is just
a mock object representing a future pipeline. Some parameters are available to adjust the execution time and
the io files to handle.

The [program](bin/processor.py) is written in python. The configuration is defined
in `~/.processor.json`. It can be run directly but it meant to be started by the wrapping launcher.

#### The requirements

* The code of the pipelines must not be modified.The *separation of concerns* principle must be strictly respected.
* A pipeline must not have to know which other pipelines are producing its inputs. Direct coupling between pipelines must stay minimal.

#### The configuration

The model used follows the *publish and subscribe* paradigm. The publishers send messages to a *fanout* exchange.
Each consumer is connected to its own separate queue managed by the server.

The [AMQP](https://www.amqp.org/) concepts are presented in
[this tutorial](https://www.rabbitmq.com/tutorials/amqp-concepts.html).

#### The programs

To get more familiar with the RabittMQ [API](https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/) a collection
of small programs has be written.

* [`Greeter`](src/main/java/org/svom/bunny/worker/Greeter.java) a simple java program used to send messages. It is a publisher. Once launched it enters a loop waiting for user's input.
If the character '*x*' or '*q*' is given the loop is stopped and the program exits. If the string '*close*' is given running consumers
are cleanly closed.

* [`greeter.py`](bin/greeter.py) is the python version of the same message producer. It is based on the
[pika](https://pika.readthedocs.io/en/0.10.0/modules/index.html) package.

* [`Patron`](src/main/java/org/svom/bunny/worker/Patron.java) is simple java program used to receive messages. It is a consumer.

* [`patron.py`](bin/patrib.py) is the python version of the same message consumer.

* [`Worker`](src/main/java/org/svom/bunny/worker/Worker.java) is a bit more complex. It can be used as a producer when run with the flag `-s` and as a consumer if the flag `-l` is set.
It can say (`-s`) something and listen (`-l`) to incoming messages.

Eventually a launcher program has been developed. It is a wrapper which takes in input a command with its arguments, executes this command when
it receives a message to trigger the process and sends a report when the command has completed.

* [`Launcher`](src/main/java/org/svom/bunny/worker/Launcher.java) implements in java this fonctionnality.To use it :

Set the `CLASSPATH` with:
 * `bunny/build/classes/main`
 * `lib/amqp-client-3.6.5.jar`
 * `.m2/repository/commons-io/commons-io/2.5/commons-io-2.5.jar`
 * `.m2/repository/commons-cli/commons-cli/1.3.1/commons-cli-1.3.1.jar`

and run

`java org.svom.bunny.worker.Launcher -v -n my-launcher "processor.py -n p1"`

then to trigger the processing :

`java org.svom.bunny.worker.Greeter`

and enter '`start`' at the prompt.

`java org.svom.bunny.worker.Greeter`

run also :

`java org.svom.bunny.worker.Patron`

to receive the message telling than the processing has finished.
The program `Worker -l` may also be used.


---