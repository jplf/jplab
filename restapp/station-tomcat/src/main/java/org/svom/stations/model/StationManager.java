//______________________________________________________________________________

//      Svom Rest workbench
//______________________________________________________________________________

package org.svom.stations.model;

import org.apache.log4j.Logger;
import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * A StationManager object manages a collection of stations.
 *
 * This object can be tested using:
 * <code>java org.svom.stations.model.StationManager</code>
 * It is a singleton.
 * It is meant to experiment REST design.
 * It makes use of the Jackson API.
 *
 * @author Jean-Paul Le Fèvre
 */
public final class StationManager {
    /**
     * The unique instance.
     */
    private static StationManager manager = null;
    /**
     * The jackson object mapper.
     */
    private ObjectMapper mapper;
    /**
     * The logger used to get messages.
     */
    private Logger logger = null;
    /**
     * The list of stations.
     */
    private List<Station> stationList;
    /**
     * The name of the json file storing the station list.
     */
    private String filename;

    //__________________________________________________________________________
    /**
     * Gets an unique instance of this manager.
     * It is a singleton
     * @return the unique instance.
     */
    public static StationManager instance() {

        if (manager == null) {
            manager = new StationManager();
        }

        return manager;
    }
    //__________________________________________________________________________
    /**
     * Creates the station manager.
     *
     * It reads the database storing the description of the VHF stations.
     * In this very simple version the database is just a json file.
     * The name of this file is harcoded in this constructor.
     * The environment variable <code>SRV_HOME</code> must be defined.
     */
    private StationManager() {

        logger = Logger.getLogger("StationManager");
        mapper = new ObjectMapper();

        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        stationList = new ArrayList<Station>(10);

        logger.info("The station manager is created");

        String srvHome = System.getenv("SRV_HOME");
        if (srvHome == null) {
            throw new RuntimeException("SRV_HOME is not defined");
        }

        filename = srvHome + "/etc/stations.json";

        try {
            readStationList();
        }
        catch (IOException ex) {
            throw new RuntimeException("File " + filename, ex);
        }
    }
    //__________________________________________________________________________
    /**
     * Gets the list of stations.
     *
     * @return the list.
     */
    public List<Station> getStationList() {

        return stationList;
     }
    //__________________________________________________________________________
    /**
     * Reads the list of stations from a json-formatted file.
     * The file is <code>SRV_HOME/etc/stations.json</code>
     * @see TypeReference
     * @throws IOException if the input file cannot be read.
     */
    public void readStationList() throws IOException {

        stationList = mapper.readValue(new File(filename),
                      new TypeReference<List<Station>>() { });
     }
    //__________________________________________________________________________
    /**
     * Writes the list of stations to a json-formatted file.
     *
     * A copy of an already existing file is done before.
     * @throws IOException if the writing fails.
     */
    public void writeStationList() throws IOException {

        File current = new File(filename);

        if (current.exists()) {
            Files.copy(Paths.get(filename), Paths.get(filename + '%'),
                       StandardCopyOption.REPLACE_EXISTING);
        }

        mapper.writeValue(current, stationList);
    }
    //__________________________________________________________________________
    /**
     * Retrieves a station by its name.
     *
     * The container is not a map so it just iterates on the list !
     *
     * @param name the name of the station to get.
     * @return the station or null if not found.
     */
    public Station getStationByName(String name) {

        if (name == null) {
            throw new IllegalArgumentException("Invalid null name");
        }

        for (Station s : stationList) {
            if (name.equals(s.getName())) {
                return s;
            }
        }

        return null;
    }
    //__________________________________________________________________________
    /**
     * Finds the station index.
     * @param name the name of the station to find.
     * @return the index or -1 if not found.
     */
    private int findStationIndex(String name) {

        if (name == null) {
            throw new IllegalArgumentException("Invalid null name");
        }

        int i = 0;
        for (Station s : stationList) {
            if (name.equals(s.getName())) {
                return i;
            }
            i++;
        }

        return -1;
    }
    //__________________________________________________________________________
    /**
     * Parses a station description.
     *
     * The list is not modified by this method : it just instantiates the
     * object without adding it in the list.
     *
     * @param data the json description of the station.
     * @return the station object.
     */
    public Station parseStationData(String data) {

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
     * Adds a station to the list.
     *
     * The station must not have the same name than another one.
     * The list is saved on disk by this method.
     *
     * @param station the station to store.
     * @return the number of stations or -1 if it fails.
     * @throws IOException if the list cannot be written down.
     */
    public int addStation(Station station) throws IOException {

        if (station == null) {
            throw new IllegalArgumentException("Invalid null station");
        }

        String name = station.getName();
        Station s = getStationByName(name);
        if (s != null) {
            return -1;
        }

        stationList.add(station);
        writeStationList();

        return stationList.size();
    }
    //__________________________________________________________________________
    /**
     * Removes a station from the list.
     *
     * The updated list is saved on disk by this method.
     * @param station the station to delete.
     * @return the number of stations.
     * @throws IOException if the list cannot be written down.
     */
    public int deleteStation(Station station) throws IOException {

        if (station == null) {
            throw new IllegalArgumentException("Invalid null station");
        }

        String name = station.getName();
        int i = findStationIndex(name);
        if (i >= 0) {
            stationList.remove(i);
            writeStationList();
        }

        return stationList.size();
    }
    //__________________________________________________________________________
    /**
     * Gets the size of the array of stations.
     * @return the number of stations.
     */
    public int getNumberOfStations() {

        return stationList.size();
    }
    //__________________________________________________________________________
    /**
     * Gets a description of this object.
     * @return the string.
     */
    public String toString() {

        StringBuilder buf = new StringBuilder("StationManager\n");
        buf.append("Number of stations: ").append(stationList.size());

        return  buf.toString();
    }
    //__________________________________________________________________________
    /**
     * Prints the list of stored stations.
     */
    public void dump() {

        for (Station s : stationList) {
            System.out.println(s);
        }
    }
    //__________________________________________________________________________
    /**
     * Checks that the manager is working as expected.
     *
     * Usage :
     * <code>java org.svom.stations.mode.StationManager</code>
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {

        try {
            StationManager mgr = instance();
            mgr.dump();
            mgr.writeStationList();
        }
        catch (Exception ex) {
            System.out.println("WTF ?");
            ex.printStackTrace();
        }

        System.exit(0);
    }
//______________________________________________________________________________
}
