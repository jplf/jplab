//______________________________________________________________________________

//      Svom Rest workbench
//______________________________________________________________________________

package org.svom.stations.webapp;

import org.apache.log4j.Logger;
import java.util.List;
import java.io.StringWriter;
import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.svom.stations.model.Station;
import org.svom.stations.model.Owner;

/**
 * A TreeFormatter object which handles json serialization.
 *
 * This object was the second attempt to understand the Jackson library.
 * See : http://www.studytrails.com/java/json/jackson-create-json.jsp
 *
 * It is a singleton. It is a different implementation from the obj formatter.
 * It makes use of the Jackson API.
 *
 * @author Jean-Paul Le Fèvre
 */
public final class TreeFormatter {
    /**
     * The unique instance.
     */
    private static TreeFormatter formatter = null;
    /**
     * The jackson object mapper.
     */
    private ObjectMapper mapper;
    /**
     * The jackson string writer.
      */
    private StringWriter writer;
    /**
     * The node factory.
     */
    private JsonNodeFactory nodeFactory;
    /**
     * The logger used to get messages.
     */
    private Logger logger = null;

    //__________________________________________________________________________
    /**
     * Gets an unique instance of this formatter.
     * It is a singleton
     * @return the unique instance.
     */
    public static TreeFormatter instance() {

        if (formatter == null) {
            formatter = new TreeFormatter();
        }

        return formatter;
    }
    //__________________________________________________________________________
    /**
     * Creates the formatter.
     *
     * It manages the json formatting of objects.
     */
    private TreeFormatter() {

        logger = Logger.getLogger("org.svom.stations.webapp");

        // Create the node factory that gives us nodes.
        nodeFactory = new JsonNodeFactory(false);
        mapper = new ObjectMapper();
        logger.info("Formatter instance");
    }
    //__________________________________________________________________________
    /**
     * Creates a generator.
     *
     * It must be called each time a string needs to be created otherwise
     * the string writer accumulates contents.
     * @return a json generator.
     */
    private JsonGenerator generate() {

        try {
            writer = new StringWriter();
            JsonFactory jsonFactory = new JsonFactory();
            JsonGenerator generator = jsonFactory.createGenerator(writer);
            generator.useDefaultPrettyPrinter();

            return generator;
        }
        catch (IOException ex) {
            logger.error("The generator failed " + ex.getMessage());
            return null;
        }
    }
    //__________________________________________________________________________
    /**
     * Gets the json string.
     * @return the generated content.
     */
    private String getJsonString() {
        return writer.toString();
    }
    //__________________________________________________________________________
    /**
     * Parses a station description.
     *
     * The list is not modified by this method : it just instantiates the
     * object without adding it in the list managed by the StationManager.
     *
     * @param data the json description of the station.
     * @return the station object.
     */
    public Station jsonRead(String data) {

        if (data == null) {
            throw new IllegalArgumentException("Invalid null data");
        }

        try {
            Station station = mapper.readValue(data,
                              new TypeReference<Station>() { });
            return station;
        }
        catch (Exception ex) {
            logger.error("Json read failed " + ex.getMessage());
            throw new RuntimeException("Invalid station data", ex);
        }
    }
    //__________________________________________________________________________
    /**
     * Gets a station as a json-formatted string.
     * All fields are written to the string.
     * The link is set.
     *
     * @param station the station to process.
     * @param path the uri of the station.
     * @return the string.
     */
    public String writeStationContent(Station station, String path) {

        ObjectNode node = nodeFactory.objectNode();

        if (path != null && !path.endsWith("/")) {
            path += '/';
        }

        node.put("href", path + station.getName());
        node.put("name", station.getName());

        Owner owner = station.getOwner();
        if (owner != null) {
            node.put("owner", owner.getName());
        }

        node.put("longitude", station.getLongitude());
        node.put("latitude", station.getLatitude());
        node.put("address", station.getAddress());

        try {
            mapper.writeTree(generate(), node);
            return getJsonString();
        }
        catch (IOException ex) {
            logger.error("Write station failed " + ex.getMessage());
            return null;
        }
    }
    //__________________________________________________________________________
    /**
     * Gets the list of stations as a json-formatted string.
     * All fields are deeply written.
     *
     * @param stationList the list to process.
     * @param path the uri of the list, '/' terminated.
     * @return the string.
     */
    public String writeStationListFields(List<Station> stationList,
                                         String path) {
        return writeStationListFields(stationList, path, true);
    }
    //__________________________________________________________________________
    /**
     * Gets the list of stations as a json-formatted string.
     * All fields are written.
     *
     * @param stationList the list to process.
     * @param path the uri of the list, '/' terminated.
     * @param deep if false only the owner href is written.
     * @return the string.
     */
    public String writeStationListFields(List<Station> stationList,
                                         String path, boolean deep) {

        ArrayNode list = nodeFactory.arrayNode();

        if (path != null && !path.endsWith("/")) {
            path += '/';
        }

        for (Station s : stationList) {

            ObjectNode node = list.addObject();

            node.put("href", path + s.getName());
            node.put("name", s.getName());

            Owner owner = s.getOwner();
            if (owner != null) {
                ObjectNode ownerNode = node.putObject("owner");
                ownerNode.put("href", path + "owners/" + owner.getName());
                if (deep) {
                    ownerNode.put("name", owner.getName());
                    ownerNode.put("email", owner.getEmail());
                }
            }

            node.put("longitude", s.getLongitude());
            node.put("latitude",  s.getLatitude());
            node.put("address",   s.getAddress());
       }

        try {
            mapper.writeTree(generate(), list);
            return getJsonString();
        }
        catch (IOException ex) {
            logger.error("Write fields failed " + ex.getMessage());
            return null;
        }
    }
    //__________________________________________________________________________
    /**
     * Gets the list of station names as a json-formatted string.
     *
     * @param stationList the list to process.
     * @return the string.
     */
    public String writeStationListNames(List<Station> stationList) {

        ArrayNode names = nodeFactory.arrayNode();

        for (Station s : stationList) {
            names.add(s.getName());
        }

        try {
            mapper.writeTree(generate(), names);
            return getJsonString();
        }
        catch (IOException ex) {
            logger.error("Write names failed " + ex.getMessage());
            return null;
        }
    }
    //__________________________________________________________________________
}
