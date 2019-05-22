//______________________________________________________________________________

//      Svom Rest workbench
//______________________________________________________________________________

package org.svom.stations.webapp;

import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.Annotated;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.svom.stations.model.Station;

/**
 * A ObjFormatter object handles json serialization.
 *
 * It is no longer used.
 *
 * This formatter was the first attempt to understand the Jackson library.
 * It makes use of new objects to read or write json strings representing
 * the objects in the model. These json objects add a href attribute to the
 * model objects. A json object must be created for each model object.
 * These objects are named <i>Linked</i> something.
 *
 * Other implementations are tested.
 * It would have been nice to define an interface and a formatter factory to
 * play with different implementations but I'm too lazy.
 *
 * It is a singleton.
 * It is meant to experiment REST design.
 * It makes use of the Jackson API.
 *
 * @author Jean-Paul Le Fèvre
 */
public final class ObjFormatter {
    /**
     * The unique instance.
     */
    private static ObjFormatter formatter = null;
    /**
     * The jackson object mapper.
     */
    private ObjectMapper mapper;
    /**
     * The jackson object writer.
     */
    private ObjectWriter writer;
    /**
     * The logger used to get messages.
     */
    private Logger logger = null;
    /**
     * Retrieves the filter id.
     * Actually I don't understand why this class is needed.
     */
    private static class AnnotationIntrospector
        extends JacksonAnnotationIntrospector {

        /**
         * Finds the id.
         * @param a the annotated.
         * @return the id.
         */
        @Override
        public Object findFilterId(Annotated a) {
            return "stationFilter";
        }
    }
    //__________________________________________________________________________
    /**
     * Gets an unique instance of this formatter.
     * It is a singleton
     * @return the unique instance.
     */
    public static ObjFormatter instance() {

        if (formatter == null) {
            formatter = new ObjFormatter();
        }

        return formatter;
    }
    //__________________________________________________________________________
    /**
     * Creates the formatter.
     *
     * It manages the json formatting of objects.
     */
    private ObjFormatter() {

        logger = Logger.getLogger("ObjFormatter");
        mapper = new ObjectMapper();
        mapper.setAnnotationIntrospector(new AnnotationIntrospector());

        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // The filter is necessary even if it does nothing otherwise
        // jackson fails since there is a true filter
        // in #writeStationListNames()
        FilterProvider filter = new SimpleFilterProvider()
            .addFilter("stationFilter",
                       SimpleBeanPropertyFilter.serializeAllExcept("none"))
            .setFailOnUnknownId(false);

        // This is the default writer with the dummy filter.
        writer = mapper.writer(filter);

        logger.info("The rest formatter is created");
    }
    //__________________________________________________________________________
    /**
     * Serializes an object as a json-formatted string.
     *
     * @param obj the object to format.
     * @return the string.
     */
    public String jsonWrite(Object obj) {

        if (obj == null) {
            throw new IllegalArgumentException("Invalid null object");
        }

        try {
            return writer.writeValueAsString(obj);
        }
        catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return "";
        }
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
            ex.printStackTrace();
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

        LinkedStation restStation = new LinkedStation(station);
        restStation.setLinks(path);
        logger.info("Got : " + restStation.toString());

        return jsonWrite(restStation);
    }
    //__________________________________________________________________________
    /**
     * Gets the list of stations as a json-formatted string.
     * All fields are written.
     *
     * @param stationList the list to process.
     * @param path the uri of the list, '/' terminated.
     * @return the string.
     */
    public String writeStationListFields(List<Station> stationList,
                                         String path) {

        List<LinkedStation> restList =
            new ArrayList<LinkedStation>(stationList.size());

        for (Station s : stationList) {

            LinkedStation restStation = new LinkedStation(s);

            restStation.setLinks(path);
            restList.add(restStation);
        }

        return jsonWrite(restList);
    }
    //__________________________________________________________________________
    /**
     * Gets the list of station names as a json-formatted string.
     *
     * @param stationList the list to process.
     * @return the string.
     */
    public String writeStationListNames(List stationList) {

        try {
            FilterProvider filter = new SimpleFilterProvider()
                .addFilter("stationFilter",
                           SimpleBeanPropertyFilter.filterOutAllExcept("name"));

            // Another writer is used.
            return mapper.writer(filter).writeValueAsString(stationList);
        }
        catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return "";
        }
    }
    //__________________________________________________________________________
}
