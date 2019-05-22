//______________________________________________________________________________

//      Svom Rest workbench
//______________________________________________________________________________

package org.svom.stations.model;

/**
 * A Owner object represents someone which owns a station.
 *
 * @author Jean-Paul Le Fèvre
 */
public class Owner {
    /**
     * The name of this owner.
     */
    private String name;
    /**
     * The email address.
     */
    private String email;

    //__________________________________________________________________________
    /**
     * Creates the owner.
     */
    public Owner() {

    }
    //__________________________________________________________________________
    /**
     * Creates the owner.
     * @param name the name to set.
     */
    public Owner(String name) {
        setName(name);
    }
    //__________________________________________________________________________
    /**
     * Duplicates an owner.
     * @param o the object to copy
     */
    public Owner(Owner o) {

        this.name  = o.name;
        this.email = o.email;
    }
    //__________________________________________________________________________
    /**
     * Gets the current attributes of this owner.
     * @return the content of this object.
     */
    @Override
    public String toString() {

        StringBuilder buf = new StringBuilder("Owner:");
        buf.append(' ').append(name);

        buf.append(" <").append(getEmail()).append('>');

        return buf.toString();
    }
    //__________________________________________________________________________
    /**
     * Updates the content of  this owner.
     *
     * If the input attributes are not null they are copied.
     *
     * @param data the new content to use.
     */
    public final void update(Owner data) {

        String s = data.getName();
        if (s != null) {
            setName(s);
        }

        s = data.getEmail();
        if (s != null) {
            setEmail(s);
        }
    }
    //__________________________________________________________________________
    /**
     * Gets the name of this owner.
     * @return the name.
     */
    public final String getName() {

        return name;
    }
    //__________________________________________________________________________
    /**
     * Sets the name of this owner.
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
     * Gets the email address of this owner.
     * @return the address.
     */
    public final String getEmail() {

        return email;
    }
    //__________________________________________________________________________
    /**
     * Sets the email address of this owner.
     * @param email the name to set.
     */
    public final void setEmail(String email) {

        this.email = email;
      }
    //__________________________________________________________________________
}
