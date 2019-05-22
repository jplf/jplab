//______________________________________________________________________________

//      Svom Rest workbench
//______________________________________________________________________________

package org.svom.stations.webapp;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import java.util.List;
import java.util.Date;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.HEAD;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.CacheControl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.svom.stations.model.StationManager;
import org.svom.stations.model.Station;

/**
 * Root resource (exposed at "stations" path)
 * The url pattern defined in web.xml must be consistent
 * with the Path below.
 *
 * The data model is managed by the StationManager object.
 *
 */
@Path("/stations")
@Api(value = "stations")
public class StationService {
    /**
     * The object managing the list of stations.
     */
    private StationManager manager;
    /**
     * The object managing the json representation of stations.
     * See also ObjFormatter.
     */
    private TreeFormatter formatter;
    /**
     * The logger used to get messages.
     */
    private Logger logger = null;
    /**
     * A time-to-live.
     */
    public static final int ONE_HOUR = 3600;
    /**
     * A debugging level.
     */
    public static int debug = 1;

    //__________________________________________________________________________
    /**
     * creates the service object.
     * the station manager and the formatter are created.
     */
    public StationService() {

        logger     = Logger.getLogger("org.svom.stations.webapp");
        manager    = StationManager.instance();
        formatter  = TreeFormatter.instance();

        logger.info("StationService built");

        Level level = logger.getLevel();
        if (level == null && logger.getParent() != null) {
            level = logger.getParent().getLevel();
        }
    }
    //__________________________________________________________________________
    /**
     * Gets the number of stations.
     *
     * Method handling HTTP HEAD requests. The response header provides
     * the size of the collection. No body is returned.
     *
     * @return the response which is only the header.
     */
    @HEAD
    public final Response getNumber() {

        CacheControl caching = new CacheControl();
        caching.setPrivate(false);

        return Response.status(Response.Status.OK)
            .cacheControl(caching)
            .header("total-count", manager.getNumberOfStations()).build();
    }
    //__________________________________________________________________________
    /**
     * Returns the list of stations.
     *
     * If the field parameter is specified only this field values are returned
     * otherwise all values are sent back. In this simple implementation
     * only <code>name</code> can be filtered.
     *
     * If the deep parameter is passed and set to false only the href of
     * the station's owner is returned otherwise the full content of the owner
     * is sent back.
     *
     * @param fields the list of fields to select or null.
     * @param deep if true all owner fields are given otherwise only the href.
     * @param uriInfo the uri parameters.
     * @return the list of stations as a json string.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Gets the list of VHF stations",
                  notes = "The list is a json object")
    public final Response getList(@QueryParam("fields") String fields,
                 @DefaultValue("true") @QueryParam("expand") boolean deep,
                 @Context UriInfo uriInfo) {

        if (fields == null) {

            List<Station> list = manager.getStationList();

            String path = uriInfo.getAbsolutePath().toString();
            String str  = formatter.writeStationListFields(list, path, deep);

            // Just to learn caching.
            CacheControl caching = new CacheControl();
            caching.setPrivate(false);
            caching.setMaxAge(ONE_HOUR);

            return Response.status(Response.Status.OK)
                .cacheControl(caching)
                .entity(str).build();
        }
        else if (fields.equals("name")) {
            // Other field selection is not (yet) implemented.
            List<Station> list = manager.getStationList();
            String str = formatter.writeStationListNames(list);

            return Response.status(Response.Status.OK).entity(str).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    //__________________________________________________________________________
    /**
     * Gets a station by name.
     *
     * @param name the name of the station.
     * @param uriInfo the uri parameters.
     * @return the description of the station in a json body.
     */
    @GET @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public final Response getStationAsJson(@PathParam("name") String name,
                                     @Context UriInfo uriInfo) {

        Station station = manager.getStationByName(name);
        if (station == null) {
            String msg = "Station " + name + " not found !";
            return
            Response.status(Response.Status.NOT_FOUND).entity(msg).build();
        }
        String etag = "H: " + String.valueOf(station.hashCode());

        String path = uriInfo.getAbsolutePath().toString();
        int idx = path.lastIndexOf(name);

        String str = formatter.writeStationContent(station,
                                                    path.substring(0, idx));

        // Just to learn caching. Parameters may be changed.
        CacheControl caching = new CacheControl();
        caching.setPrivate(false);
        caching.setMaxAge(ONE_HOUR);
        caching.setNoTransform(false);

        return Response.status(Response.Status.OK)
            .lastModified(new Date())
            .tag(etag)
            .cacheControl(caching)
            .entity(str).build();
    }
    //__________________________________________________________________________
    /**
     * Gets a station by name.
     *
     * @param name the name of the station to fetch.
     * @return the description of the station in a plain text body.
     */
    @GET @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public final Response getStationAsString(@PathParam("name") String name) {

        Station station = manager.getStationByName(name);
        if (station == null) {
            String msg = "Station " + name + " not found !";
            return
            Response.status(Response.Status.NOT_FOUND).entity(msg).build();
        }

        return Response.status(Response.Status.OK)
            .entity(station.toString()).build();
    }
    //__________________________________________________________________________
    /**
     * Deletes a station identified by its name.
     * If it does not exist does nothing.
     * @param name the name of the station to remove.
     * @return the description of the station.
     */
    @DELETE @Path("/{name}")
    public final Response deleteStation(@PathParam("name") String name) {

        try {
            Station station = manager.getStationByName(name);
            if (station != null) {
                manager.deleteStation(station);
                // Otherwise nothing to do.
            }

            return Response.status(Response.Status.NO_CONTENT)
                .header("total-count", manager.getNumberOfStations()).build();
        }
        catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("WTF ? " + ex.getMessage()).build();
        }
    }
    //__________________________________________________________________________
    /**
     * Creates or updates a station.
     *
     * If it exists it is updated.
     * Note that the difference between put and post are not
     * fully correctly managed.
     *
     * @param stationData the description of the station.
     * @param qname the name of the station to update.
     * @param uriInfo the uri parameters.
     * @return the description of the station.
     * @see #postData()
     */
    @PUT @Path("/{data}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public final Response putData(String stationData,
                            @PathParam("data") String qname,
                            @Context UriInfo uriInfo) {

        // Double quotes are not managed -> qname == "name"

        try {
            Station data = manager.parseStationData(stationData);
            String name  = data.getName();

            if (qname == null || !qname.equals('"' + name + '"')) {
                logger.warn("putData is " + name);
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid name: " + qname).build();
            }

            Station s = manager.getStationByName(name);
            Response response = null;
            String path = uriInfo.getAbsolutePath().toString();
            int idx = path.lastIndexOf("/");
            path = path.substring(0, idx);

            if (s == null) {
                // The station is created.
                data.setCreationDate(null);
                int n  = manager.addStation(data);
                String str = formatter.writeStationContent(data, path);

                 response  = Response.status(Response.Status.CREATED)
                    .header("total-count", n)
                    .header("Location", name)
                    .entity(str).build();
            }
            else {
                // Theoretically the content of data should be complete
                // to make sure this PUT is idempotent.
                s.update(data);
                String str = formatter.writeStationContent(s, path);
                response   = Response.status(Response.Status.OK)
                    .header("total-count", manager.getNumberOfStations())
                    .entity(str).build();
            }

            manager.writeStationList();

            return response;
        }
        catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("WTF ? " + ex.getMessage()).build();
        }
    }
    //__________________________________________________________________________
    /**
     * Adds a new station or updates an existing one.
     *
     * @param stationData the json description of the new station.
     * @param uriInfo the uri parameters.
     * @return the description of the station.
     * @see #putData()
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public final Response postData(String stationData,
                                   @Context UriInfo uriInfo) {

        try {
            Station data = formatter.jsonRead(stationData);
            String name  = data.getName();

            if (name == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid null name").build();
            }

            Station s = manager.getStationByName(name);
            Response response = null;
            String path = uriInfo.getAbsolutePath().toString();

            if (s == null) {
               // The station is created.
                data.setCreationDate(null);
                int n  = manager.addStation(data);
                String str = formatter.writeStationContent(data, path);

                response  = Response.status(Response.Status.CREATED)
                    .header("total-count", n)
                    .header("Location", path + '/' + name)
                    .entity(str).build();
            }
            else {
                // The existing station is updated.
                s.update(data);
                String str = formatter.writeStationContent(s, path);
                response  = Response.status(Response.Status.OK)
                    .header("total-count", manager.getNumberOfStations())
                    .entity(str).build();
            }

            manager.writeStationList();

            return response;
        }
        catch (IOException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                   .entity("Invalid data").build();
        }
        catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("WTF ? " + ex.getMessage()).build();
        }
    }
//______________________________________________________________________________
}
