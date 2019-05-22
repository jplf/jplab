### Svom Rest workbench - Some useful tips.

___
Created May 2015 - Jean-Paul Le Fèvre <jean-paul.lefevre@cea.fr> - CEA Irfu
___

This software is governed by the [CeCILL license](http://www.cecill.info/index.en.html)

More [documentation](https://drf-gitlab.cea.fr/lefevre/jplf/wikis/station-service.md) is available.
See also [StationTomcat](https://drf-gitlab.cea.fr/lefevre/jplf/wikis/station-tomcat.md)

#### Initialization

This version is based on the Grizzly http server.
````
mvn archetype:generate \
-DarchetypeArtifactId=jersey-quickstart-grizzly2 \
-DarchetypeGroupId=org.glassfish.jersey.archetypes -DinteractiveMode=false \
-DgroupId=org.svom -DartifactId=station-service \
-Dpackage=org.svom.stations.webapp \
-DarchetypeVersion=2.17
````
#### Maven targets
```
mvn clean   
mvn compile   
mvn checkstyle:check   
mvn exec:java   
```
Or
````
mvn assembly:assembly   
mv station-service.jar-jar-with-dependencies.jar station-service.jar   
java -jar target/station-service.jar -Djava.util.logging.config.file=etc/logging.properties
````
### Usage

Set the environment variable `SRV_HOME` with a value being the path to the top directory of this code.
The list of stations will be found in the file `$SRV_HOME/etc/stations.json`


The URL of the server is hardcoded in the file `Main.java`


Start the server either with maven (*mvn exec:java*) or directly


Make sure that both server and client agree on whether the TLS version is on or off. It is also hardcoded in the server and in the client.

Run the client : *client.sh* 
But maybe the design of this script is not excellent :(  
There is also a perl(1) version : *client.pl*  
The credential must be given before other options. Values are hardcoded in 
the file *AccessFilter.java*

-h : *help*  
-u : *username*  
-m : *mot-de-passe*  

-i : *info (number of stations in header)*  
-l : *list (array of station contents with full owner content)*  
-L : *list (array of station contents with only owner href)*  
-n : *list (array of names)*  

-p file.json : *post (new or changed station)*  
-P file.json : *put (create new station)*  
-g name : *get (station attributes)*  
-G name : *get (station attributes) with test on Accept header*  
-d name : *delete (station)*  

#### More info

On the [wiki](https://drf-gitlab.cea.fr/lefevre/jplf/wikis/station-service.md) there are more explanations.

#### TLS
The certificate of the server must be generated with commands below.
The *rest-server.pem* file is given to *curl(1)* to allow the verification of the server certificate which is created
in the file *Main.java* by some hard-coded instructions.
````
keytool -genkeypair -alias rest-keys -keypass passwd -storepass passwd -keystore keystore.jks  
keytool -export -alias rest-keys -storepass passwd -keystore keystore.jks -file rest-server.crt  
keytool -printcert -file rest-server.crt  
keytool -list -keystore keystore.jks -storepass passwd  
openssl x509 -inform der -in rest-server.crt -out rest-server.pem  
openssl x509 -in rest-server.pem -text  
keytool -printcert -file rest-server.pem  
````