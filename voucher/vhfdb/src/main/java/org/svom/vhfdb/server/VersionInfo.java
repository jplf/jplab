//______________________________________________________________________________

//      Svom  workbench - Voucher project
//______________________________________________________________________________

package org.svom.vhfdb.server;

/**
 * Provides information about the current release.
 * <br>
 * It is a singleton.
 * @author Jean-Paul Le Fèvre
 */
//______________________________________________________________________________

public final class VersionInfo implements java.io.Serializable {
    /**
     * The version ID.
     */
    public static final String VERSION = "0.1";
    /**
     * The Release date.
     */
    public static final String DATE = "30 August 2016";
    /**
     * The author.
     */
    public static final String AUTHOR = "J-P.LeFevre@cea.fr";
    /**
     * The application.
     */
    public static final String APPLI = "Vhfdb";
    /**
     * The unique instance.
     */
    private  static VersionInfo info = null;

    //__________________________________________________________________________
    /**
     * Gets an unique instance of the info.
     * <br>
     * It is a singleton
     * @return the unique instance.
     */
    public static VersionInfo instance() {
        if (info == null) {
            info = new VersionInfo();
        }

        return info;
    }
    //__________________________________________________________________________
    /**
     * Creates the VersionInfo.
     */
    private VersionInfo() {
    }
    //__________________________________________________________________________
    /**
     * Prints info on standard output.
     */
    public void print() {
        System.out.println(instance().toString());
    }
    //__________________________________________________________________________
    /**
     * Returns the version.
     * @return the string.
     */
    public String getVersion() {
        return VERSION;
    }
    //__________________________________________________________________________
    /**
     * Returns the date.
     * @return the string.
     */
    public String getDate() {
        return DATE;
    }
    //__________________________________________________________________________
    /**
     * Returns info in a string.
     * @return the string.
     */
    public String toString() {
        StringBuilder buf = new StringBuilder(APPLI);
        buf.append(" package : version ");
        buf.append(VERSION).append(" from ").append(DATE);
        return buf.toString();
    }
    //__________________________________________________________________________
    /**
     * Prints version number on standard output.
     * @param args the array of options.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.print(VERSION);
        }
        else if (args[0].equals("-d")) {
            System.out.println(VersionInfo.instance().toString());
        }
    }
    //__________________________________________________________________________
}


