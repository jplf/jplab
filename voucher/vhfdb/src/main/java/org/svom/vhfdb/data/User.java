//______________________________________________________________________________

//      Svom workbench - Voucher project
//______________________________________________________________________________

package org.svom.vhfdb.data;

/**
 * User : a bean storing a person's identity.
 *
 * @author Jean-Paul Le Fèvre
 */
//______________________________________________________________________________

public class User {
    /**
     * @serial The person's name.
     */
    private String username;
    /**
     * @serial The person's login.
     */
    private String login;
    /**
     * @serial The person's profile.
     */
    private int profile;
    /**
     * @serial The existing profile.
     */
    protected static final int PLAIN_USER = 0;
    /**
     * @serial The existing profile.
     */
    protected static final int SUPER_USER = 1;

    //__________________________________________________________________________
    /**
     * Creates a user for nobody.
     */
    public User() {
        this("nobody");
    }
    //__________________________________________________________________________
    /**
     * Creates a user.
     * @param login the person's login.
     */
    public User(String login) {
        this(login, "John Doe");
    }
    //__________________________________________________________________________
    /**
     * Creates a user.
     * @param username the person's name.
     * @param login the person's login.
     */
    public User(String login, String username) {
        this.username = username;
        this.login    = login;
        this.profile  = PLAIN_USER;
    }
    //__________________________________________________________________________
    /**
     * Creates a string giving the content of the object.
     * @return the string.
     */
    public String toString() {
        StringBuilder buf = new
            StringBuilder("Name : ").append(String.valueOf(getUsername()));
        buf.append(", login : ").append(String.valueOf(getLogin()));
        buf.append(", profile : ").append(String.valueOf(getProfile()));

        return buf.toString();
    }
    //__________________________________________________________________________
    /**
     * Gets the name.
     * @return the string.
     */
    public final String getUsername() {
        return username;
    }
    //__________________________________________________________________________
    /**
     * Sets the name.
     * @param username the name.
     */
    public final void setUsername(String username) {
        this.username = username;
    }
    //__________________________________________________________________________
    /**
     * Gets the login.
     * @return the string.
     */
    public final String getLogin() {
        return login;
    }
    //__________________________________________________________________________
    /**
     * Sets the login.
     * @param login the login.
     */
    public final void setLogin(String login) {
        this.login = login;
    }
    //__________________________________________________________________________
    /**
     * Gets the profile.
     * @return the value.
     */
    public final int getProfileValue() {
        return profile;
    }
    //__________________________________________________________________________
    /**
     * Gets the profile.
     * @return the string.
     */
    public final String getProfile() {

        switch (getProfileValue()) {
        case PLAIN_USER :
            return "plain user";
        case SUPER_USER :
            return "super user";
        default :
            return "undefined profile";
        }
    }
    //__________________________________________________________________________
    /**
     * Sets the profile.
     * @param profile the profile.
     */
    public final void setProfile(String profile) {
        int value = PLAIN_USER;

        if (profile == null) {
            setProfile(value);
        }

        profile = profile.toLowerCase();

        if (profile.startsWith("super")) {
            value = SUPER_USER;
        }

        setProfile(value);
    }
    //__________________________________________________________________________
    /**
     * Sets the profile.
     * @param profile the profile.
     */
    public final void setProfile(int profile) {
        this.profile = profile;
    }
    //__________________________________________________________________________
    /**
     * Starts the test program.
     * @param args the array of arguments.
     */
    public static void main(String[] args) {
        int i     = 0;
        User user = new User();

        while (i < args.length && args[i].startsWith("-")) {

            String arg = args[i++];
            int j;

            for (j = 1; j < arg.length(); j++) {

                char flag = arg.charAt(j);

                switch (flag) {
                case 'u' :              // User name
                    user.setUsername(args[i++]);
                    break;

                case 'l':
                    user.setLogin(args[i++]);
                    break;

                case 'p':
                    user.setProfile(args[i++]);
                    break;

                default :
                    System.out.println("Options : -u name -l login -p profile");
                    System.exit(0);
                }
            }
        }

        System.out.println("User : " + user.toString());
        System.exit(0);
    }
    //__________________________________________________________________________
}
