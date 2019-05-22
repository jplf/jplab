//______________________________________________________________________________

//      Svom Rest workbench
//______________________________________________________________________________

package org.svom.stations.model;

import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.net.InetAddress;

/**
 * A Station object is a representation of a VHF receiver station.
 *
 * @author Jean-Paul Le Fèvre
 */
public class Station {
    /**
     * The name of this station.
     */
    private String name;
    /**
     * The owner of this station.
     */
    private Owner owner;
    /**
     * The longitude of the location.
     */
    private Float longitude;
    /**
     * The latitude of the location.
     */
    private Float latitude;
    /**
     * The IP address.
     */
    private InetAddress ipAddress;
    /**
     * The date of creation.
     */
    private Date created;
    /**
     * The creation date format.
     */
    private static final SimpleDateFormat D_FORMAT =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

    static {
        D_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    //__________________________________________________________________________
    /**
     * Creates the station.
     * The creation date is not set.
     */
    public Station() {

    }
    //__________________________________________________________________________
    /**
     * Duplicates a station.
     * @param s the station to copy
     */
    public Station(Station s) {

        this.name      = s.name;
        this.owner     = s.owner;
        this.longitude = s.longitude;
        this.latitude  = s.latitude;
        this.ipAddress = s.ipAddress;
        this.created   = s.created;
    }
    //__________________________________________________________________________
    /**
     * Creates the station.
     * @param name the name to set.
     */
    public Station(String name) {
        setName(name);
    }
    //__________________________________________________________________________
    /**
     * Gets the current attributes of this station.
     * @return the content of this object.
     */
    @Override
    public String toString() {

        StringBuilder buf = new StringBuilder("Station:");
        buf.append(' ').append(name);
        buf.append(' ').append(getCreationDate());

        // If only one of long, lat is null no location.
        if (longitude == null || latitude == null) {
            buf.append(" location: undef");
        }
        else {
            buf.append(String.format(" location: %f, %f ",
                                     longitude, latitude));
        }

        buf.append(" IP: ").append(getAddress());
        if (owner != null) {
            buf.append(" owner: ").append(owner.toString());
        }

        return buf.toString();
    }
    //__________________________________________________________________________
    /**
     * Updates the content of  this station.
     *
     * If the input attributes are not null they are copied.
     * The creation date is left untouched.
     * The name can be changed but it may screw up the manager.
     * @param data the new content to use.
     */
    public final void update(Station data) {

        String s = data.getName();
        if (s != null) {
            setName(s);
        }

        Owner o = data.getOwner();
        if (o != null) {
            setOwner(o);
        }

        Float l = data.getLongitude();
        if (l != null) {
            setLongitude(l);
        }
        l = data.getLatitude();
        if (l != null) {
            setLatitude(l);
        }

        s = data.getAddress();
        if (s != null) {
            setAddress(s);
        }
    }
    //__________________________________________________________________________
    /**
     * Gets the name of this station.
     * @return the name.
     */
    public final String getName() {

        return name;
    }
    //__________________________________________________________________________
    /**
     * Sets the name of this station.
     * @param name the name to set.
     */
    public final void setName(String name) {

        if (name != null) {
            this.name = name;
        }
        else {
            throw new IllegalArgumentException("Invalid null name");
        }
    }
    //__________________________________________________________________________
    /**
     * Gets the owner of this station.
     * @return the owner.
     */
    public final Owner getOwner() {

        return owner;
    }
    //__________________________________________________________________________
    /**
     * Sets the owner of this station.
     * @param owner the owner to set.
     */
    public final void setOwner(Owner owner) {

        this.owner = owner;
    }
    //__________________________________________________________________________
    /**
     * Sets the creation date of this station.
     * If the given date is null set the date to now.
     * Format ISO 8601 zone UTC
     * @param creationDate the date to set.
     * @throws ParseException if the date is not correct
     */
    public final void setCreationDate(String creationDate)
        throws ParseException {

        if (creationDate == null || creationDate.length() < 1) {
            created = new Date();
        }
        else {
            created = D_FORMAT.parse(creationDate);
        }
    }
    //__________________________________________________________________________
    /**
     * Gets the creation date as a string in ISO standard format.
     * @return the string.
     */
    public final String getCreationDate() {

        return (created == null) ? "" : D_FORMAT.format(created);
    }
    //__________________________________________________________________________
    /**
     * Gets the longitude of this station.
     * @return the value in degrees.
     */
    public final Float getLongitude() {

        return longitude;
    }
    //__________________________________________________________________________
    /**
     * Sets the longitude of this station.
     * @param longitude the value in degrees.
     */
    public final void setLongitude(float longitude) {

        if (-180.0 < longitude || longitude < +180.0) {
            this.longitude = longitude;
        }
        else {
            throw new IllegalArgumentException(
                      "Invalid longitude value: " + longitude);
        }
    }
    //__________________________________________________________________________
    /**
     * Gets the latitude of this station.
     * @return the value in degrees.
     */
    public final Float getLatitude() {

        return latitude;
    }
    //__________________________________________________________________________
    /**
     * Sets the latitude of this station.
     * @param latitude the value in degrees.
     */
    public final void setLatitude(float latitude) {

        if (-90.0 < latitude || latitude < +90.0) {
            this.latitude = latitude;
        }
        else {
            throw new IllegalArgumentException(
                      "Invalid latitude value: " + latitude);
        }
    }
    //__________________________________________________________________________
    /**
     * Gets the IP address of this station.
     * @return the four octets.
     */
    public final String getAddress() {

        return (ipAddress == null) ? "" : ipAddress.getHostAddress();
    }
    //__________________________________________________________________________
    /**
     * Sets the IP address of this station.
     * @param host the hostname or the IP address.
     * @see InetAddress#getByName()
     */
    public final void setAddress(String host) {

        try {
            ipAddress = InetAddress.getByName(host);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException(
                      "Invalid host address: " + host);
        }
    }
//______________________________________________________________________________
}
