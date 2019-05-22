//______________________________________________________________________________

//      Svom Rest workbench
//______________________________________________________________________________

package org.svom.stations.webapp;

import org.svom.stations.model.Station;
import org.svom.stations.model.Owner;

/**
 * A Station object is a representation of a VHF receiver station.
 * This object adds a link to the base object. It is used by the first
 * implementation of the json formatter.
 *
 * @author Jean-Paul Le Fèvre
 */
public final class LinkedStation extends Station implements Linkable {
    /**
     * The link which can be used to access this object.
     */
    private String href;

    //__________________________________________________________________________
    /**
     * Creates the station.
     */
    public LinkedStation() {
        super();
    }
    //__________________________________________________________________________
    /**
     * Creates a linked station from a plain station.
     * @param s the station to copy.
     */
    public LinkedStation(Station s) {
        super(s);
        Owner owner = getOwner();
        if (owner != null) {
            LinkedOwner linkedOwner = new LinkedOwner(owner);
            setOwner(linkedOwner);
        }
    }
    //__________________________________________________________________________
    /**
     * Gets the current attributes of this owner.
     * @return the content of this object.
     */
    @Override
    public String toString() {

        return super.toString() + " s linked";
    }
    //__________________________________________________________________________
    /**
     * Gets the link to this station.
     *
     * It is used by the server showing a REST interface.
     * This simple implementation avoids the Jackson bloated API.
     * This field can be undefined.
     *
     * @return the full URI.
     */
    public String getHref() {

        return href;
    }
    //__________________________________________________________________________
    /**
     * Sets the link to this station.
     * @param href the URI to set.
     */
    public void setHref(String href) {

        this.href = href;
    }
    //__________________________________________________________________________
    /**
     * Sets the recursively the links for this station.
     * @param path the used to the set the links.
     */
    public void setLinks(String path) {

        if (!path.endsWith("/")) {
            path += '/';
        }

        setHref(path + getName());

        Owner owner = getOwner();
        if (owner != null && owner instanceof LinkedOwner) {
            ((LinkedOwner) owner).setLinks(path);
        }
    }
//______________________________________________________________________________
}
