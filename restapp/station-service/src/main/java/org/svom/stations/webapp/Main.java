//______________________________________________________________________________

//      Svom Rest workbench
//______________________________________________________________________________

package org.svom.stations.webapp;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;

import java.io.IOException;
import java.net.URI;

import org.svom.stations.model.StationManager;

/**
 * Main class.
 * Started by
 * <code>mvn exec:java
 * -Djava.util.logging.config.file=etc/logging.properties</code>
 *
 */
public final class Main {
    /**
     * Base URI the Grizzly HTTP server will listen on.
     */
    public static final String BASE_URI = "http://localhost:8080/vhf/";
    /**
     * SSL URI the Grizzly HTTP server will listen on.
     */
    public static final String SSL_URI = "https://localhost:8443/vhf/";

    //__________________________________________________________________________
    /**
     * Creates the object.
     */
    private Main() {

    }
    //__________________________________________________________________________
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined
     * in this application.
     * It is the secure https version.
     *
     * @return Grizzly HTTP server.
     */
    public static HttpServer startSslServer() {
        // Create a resource config that scans for JAX-RS resources
        // and providers in org.svom.stations.webapp package.
        final ResourceConfig rc = new ResourceConfig().packages(
                                  "org.svom.stations.webapp");
        rc.register(AccessFilter.class);

        // Create and start a new instance of grizzly http server
        // exposing the Jersey application at SSL_URI

        // See: api=org.glassfish.grizzly.ssl.SSLEngineConfigurator
        // from http://www.programcreek.com/java-api-examples/index.php?

        SSLContextConfigurator sslContext = new SSLContextConfigurator();
        sslContext.setKeyStoreFile("etc/keystore.jks");
        sslContext.setKeyStorePass("gamma_ray");
        sslContext.setTrustStoreFile("etc/truststore.jsk");
        sslContext.setTrustStorePass("gamma_ray");

        SSLEngineConfigurator sslEngine = new SSLEngineConfigurator(sslContext);
        sslEngine.setClientMode(false).setNeedClientAuth(false);

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(SSL_URI),
               rc, true, sslEngine);
    }
    //__________________________________________________________________________
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined
     * in this application.
     * It is the plain http version which can be used
     * instead of the tls version.
     *
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources
        // and providers in org.svom.stations.webapp package
        final ResourceConfig rc = new ResourceConfig().packages(
                                  "org.svom.stations.webapp");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI),
                                                         rc);
    }
    //__________________________________________________________________________
    /**
     * Main method.
     * @param args the arguments of the program.
     * @throws IOException if the server cannot be started.
     */
    public static void main(String[] args) throws IOException {

        StationManager manager = StationManager.instance();

        final HttpServer server = startServer();
        System.out.println(String.format(
               "Jersey app started with WADL available at "
             + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));

        System.in.read();
        server.stop();
    }
//______________________________________________________________________________
}

