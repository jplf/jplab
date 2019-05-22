//______________________________________________________________________________

//      Svom Rest application workbench
//______________________________________________________________________________

package org.svom.stations.webapp;

import org.glassfish.jersey.server.ResourceConfig;
import javax.ws.rs.ApplicationPath;

import org.apache.log4j.Logger;
/**
 * It is the root resource (exposed at "/" path).
 */
@ApplicationPath("/")
public class StationApp extends ResourceConfig {
    /**
     * The logger used to get messages.
     */
    private Logger logger = null;
    /**
     * Makes the package available to the application.
     */
    public StationApp() {

        packages("org.svom.stations.webapp");

        logger = Logger.getLogger("org.svom.stations.webapp");
        register(logger);
        // Seems to be necessary (!)
        register(AccessFilter.class);

        register(io.swagger.jaxrs.listing.ApiListingResource.class);
        register(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        logger.info("StationService application ready");
    }
//______________________________________________________________________________
}
