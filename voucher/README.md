**Voucher** is the place where I store my OAuth test code.

### Motivation

For the science ground segment of Svom users authentication is a serious issue since there
will be many people interacting with the system, they will belong to many different categories and they will be
located everywhere in the world.

The [OAuth2](https://oauth.net/2/) protocol looks very promising to provide the features needed by Svom.
The programs found in this directory are meant to learn the basics of the technology and to evaluate the various
libraries available implementing the specifications.

### Gradle

At the same time a new build tool [Gradle](https://gradle.org/) is being tested. It is a good alternative
to [Maven](https://maven.apache.org/), which is one of the tools I hate the most.

For instance the application `vhfdb` is controlled by `gradle`. The
[README](https://drf-gitlab.cea.fr/lefevre/jplf/blob/master/voucher/vhfdb/README.md)
gives some hints about how to start working with this tool.

### The vhfdb webapp

A simple web application, à la *hello world*, is implemented to play the role of the
future Svom applications. It is a service based on *servlets* written in java using the
Apache [Tomcat](https://tomcat.apache.org/tomcat-8.0-doc/index.html) API.

Of course this application is built using `gradle`. When it runs it is available
from this [URL](http://irfupce211:8800/vhfdb) but, by nature, it is down most of the time.

#### Basic authentication

Firstly, the [basic authentication](https://en.wikipedia.org/wiki/Basic_access_authentication)
method has been implemented. The processing is done in this
[filter](/voucher/vhfdb/src/main/java/org/svom/vhfdb/server/CheckInFilter.java).

Note that a browser keeps track of any given credential making debugging difficult. When testing and debugging it is better to use `curl` :

`curl -u compte:mot2passe http://irfupce211:8800/vhfdb/login | tidy -q`

#### Token based authentication

The principle of the token base authentication is described on
[this page](https://scotch.io/tutorials/the-ins-and-outs-of-token-based-authentication).
The token which is exchanged is a Json [web token](https://scotch.io/tutorials/the-anatomy-of-a-json-web-token).

A user known by the server is described in the
[User](/voucher/vhfdb/src/main/java/org/svom/vhfdb/data/User.java) object. This object is created when authentication is successful
otherwise the methods in charge of the creation return `null`.

Most of the authentication processing is done by the
[authorizer](/voucher/vhfdb/src/main/java/org/svom/vhfdb/server/Authorizer.java). The code is based
on the [Java JWT](https://github.com/jwtk/jjwt) package.

To test this implementation start the Tomcat web server if necessary, deploy `vhfdb.war` either manually or using the Tomcat manager interface.
With a browser try to get the page `http://irfupce211:8800/vhfdb/login` by providing a password. To examine more specifically what's going on
between a client and the server use `curl`. For instance the shell script `client.sh` launches a first request with a basic authentication
credential. The response returns a token which can be used in next requests.

#### Warning

A couple of important things are left behind for sake of simplicity in this demo. The protocol is the plain *http* but *https* must be used in
real situation. The secrets used to sign the tokens are hardcoded in `Authorizer.java` which will not be acceptable. And finally all
the complexity of the cryptography algorithms has not been investigated : choices made for the demo come from examples found in the internet.

---