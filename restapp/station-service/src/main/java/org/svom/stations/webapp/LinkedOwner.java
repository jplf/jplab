//______________________________________________________________________________

//      Svom Rest workbench
//______________________________________________________________________________

package org.svom.stations.webapp;

import org.svom.stations.model.Owner;

/**
 * An Owner object is a representation of a station owner.
 * This object adds a link to the base object. It is used by the
 * object formatter.
 *
 * @author Jean-Paul Le Fèvre
 */
public final class LinkedOwner extends Owner implements Linkable {
    /**
     * The link which can be used to access this object.
     */
    private String href;

    //__________________________________________________________________________
    /**
     * Creates the owner.
     */
    public LinkedOwner() {
        super();
    }
    //__________________________________________________________________________
    /**
     * Creates a linked owner from a plain owner.
     * @param o the owner to copy.
     */
    public LinkedOwner(Owner o) {
        super(o);
    }
    //__________________________________________________________________________
    /**
     * Gets the current attributes of this owner.
     * @return the content of this object.
     */
    @Override
    public String toString() {

        return super.toString() + " o linked";
    }
    //__________________________________________________________________________
    /**
     * Gets the link to this owner.
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
     * Sets the link to this owner.
     * @param href the URI to set.
     */
    public void setHref(String href) {

        this.href = href;
    }
    //__________________________________________________________________________
    /**
     * Sets the recursively the links for this owner.
     * @param path the used to the links.
     */
    public void setLinks(String path) {

        if (!path.endsWith("/")) {
            path += '/';
        }

        setHref(path + getName());
    }
//______________________________________________________________________________
}
