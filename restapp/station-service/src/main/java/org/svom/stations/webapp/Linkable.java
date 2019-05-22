//______________________________________________________________________________

//      Java XMM XXL db application
//______________________________________________________________________________

package org.svom.stations.webapp;

/**
 * A object linkable interface.
 *
 * Manages the URI of an object.
 *
 * @author Jean-Paul Le Fèvre
 */

public interface Linkable  {

//______________________________________________________________________________
/**
 * Gets the URI.
 * @return the absolute Href.
 */
    String getHref();
//______________________________________________________________________________
/**
 * Sets the URI.
 * @param href the absolute Href.
 */
    void setHref(String href);
//______________________________________________________________________________
}
