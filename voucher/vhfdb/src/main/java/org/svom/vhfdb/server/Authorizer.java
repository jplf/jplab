//______________________________________________________________________________

//      Svom workbench - Voucher project
//______________________________________________________________________________

package org.svom.vhfdb.server;

import org.svom.vhfdb.data.User;

import java.util.Base64;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.AbstractMap.SimpleEntry;
import java.security.Key;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

/**
 * Manages authorizations.
 *
 * Usage:
 * java org.svom.vhfdb.server.Authorizer [-h]
 *
 * To test these functions run the program with these options:
 *
 * -D : dumps content of this object.
 * -b login:password : checks the validity to the basic account string.
 * -g login : get the web token for the given login.
 * -t token : checks the validity of the token.
 *
 * Example :
 * java org.svom.vhfdb.server.Authorizer -g lefevre > token
 * java org.svom.vhfdb.server.Authorizer -t `cat token`
 *
 * WARNING : this object's password as well as the salt value are hardcoded
 * in this file in order to simplify the tests. This must be changed !
 *
 * It is a singleton.
 * @author Jean-Paul Le Fèvre
 */
//______________________________________________________________________________

public final class Authorizer {
    /**
     * The unique instance.
     */
    private  static Authorizer authorizer = null;
    /**
     * The list of accounts.
     */
    private List<Map.Entry<String, String>> accounts;
    /**
     * The key used to sign token.
     */
    private Key realmKey;
    /**
     * Logger for this class and subclasses.
     */
    private Log logger = LogFactory.getLog("org.svom.vhfdb");
    /**
     * The verbosity.
     */
    private boolean debug = false;
    /**
     * The number of bytes in the salt array.
     */
    private static final int SALT_LENGTH = 20;
    /**
     * The size of the key.
     */
    private static final int KEY_LENGTH = 128;
    /**
     * The number of iterations used to get the key.
     */
    private static final int KEY_ITER = 999;

    //__________________________________________________________________________
    /**
     * Gets an unique instance of the authorizer.
     * <br>
     * It is a singleton
     * @return the unique instance.
     */
    public static Authorizer instance() {

        if (authorizer == null) {
            authorizer = new Authorizer();
        }

        return authorizer;
    }
    //__________________________________________________________________________
    /**
     * Creates the Authorizer.
     * The list of known accounts is built.
     */
    private Authorizer() {

        loadUserDb();
        makeRealmKey("gamma_ray");
    }
    //__________________________________________________________________________
    /**
     * The private key for the realm is generated.
     *
     * The salt value is fixed and constant to get the same key
     * each time the program is run.
     * @param realmPw the password of the realm
     */
    private void makeRealmKey(String realmPw) {

        try {
            String saltHandful = "The salt value is fixed and constant";

            // Build the key used by json web tokens
            SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            // This must be fixed in a production environment.
            KeySpec spec = new PBEKeySpec(realmPw.toCharArray(),
                                          generateSalt(saltHandful),
                                          KEY_ITER, KEY_LENGTH);

            realmKey = factory.generateSecret(spec);
        }
        catch (Exception ex) {
            logger.info("Can't generate the realm key");
            ex.printStackTrace();
            realmKey = null;
        }
    }
    //__________________________________________________________________________
    /**
     * Gets a salt value used to generate the key of the realm.
     *
     * If salt is null call a random number generator.
     * @param salt a string used as a salt.
     * @return an array of (random) bytes.
     */
    private byte[] generateSalt(String salt) {

        byte[] bytes = new byte[SALT_LENGTH];
        if (salt == null) {
            bytes = new byte[SALT_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(bytes);
        }
        else {
            bytes = salt.getBytes();
        }

        return bytes;
    }
    //__________________________________________________________________________
    /**
     * Creates a the database of users.
     * The list of known accounts is built.
     */
    private void loadUserDb() {

        // A tiny database of users
        accounts = new ArrayList<>();
        accounts.add(new SimpleEntry<>("lefevre", "antoine"));
        accounts.add(new SimpleEntry<>("svompal", "mot2passe"));
    }
    //__________________________________________________________________________
    /**
     * Checks if the given string identifies a valid user.
     * @param account the account string to verify.
     * @return the authenticated user or null.
     */
    protected User basicCheck(String account) {

        byte[] s = Base64.getDecoder().decode(account);
        String credentials = new String(s);

        int p = credentials.indexOf(":");
        if (p < 1) {
            return null;
        }

        String login  = credentials.substring(0, p).trim();
        String passwd = credentials.substring(p + 1).trim();

        if (!checkPassword(login, passwd)) {
            return null;
        }

        User user = new User(login);

        return user;
    }
    //__________________________________________________________________________
    /**
     * Checks if the given token identifies a valid user.
     *
     * @param token the json web token string to verify.
     * @return the authenticated user or null.
     */
    protected User tokenCheck(String token) {

        try {
            Jws<Claims> jws =
                Jwts.parser().setSigningKey(realmKey).parseClaimsJws(token);
            logger.info(String.valueOf(jws));

            String login = jws.getBody().getSubject();
            User user = new User(login);

            return user;
         }
        catch (SignatureException ex) {
            logger.info("Wrong signature", ex);
            return null;
        }
    }
    //_________________________________________________________________________
    /**
     * Sets the debug flag.
     */
    public void setDebug() {
        debug = true;
    }
    //_________________________________________________________________________
    /**
     * Returns authorizer description in a string.
     * @return the string.
     */
    public String toString() {

        StringBuilder buf = new StringBuilder("Authorizer\n");

        Iterator<Map.Entry<String, String>> it = accounts.iterator();

        for (; it.hasNext();) {

            Map.Entry<String, String> e = it.next();

            buf.append(e.getKey()).append(" : ").append(e.getValue());
            buf.append('\n');
        }

        return buf.toString();
    }
    //__________________________________________________________________________
    /**
     * Compute a token associated to a valid user.
     *
     * @param user the user object.
     * @return the token string.
     */
    protected String getToken(User user) {

        if (user == null || realmKey == null) {
            return null;
        }

        String token = Jwts.builder()
            .setSubject(user.getLogin())
            .signWith(SignatureAlgorithm.HS512, realmKey)
            .compact();

        return token;
    }
    //__________________________________________________________________________
    /**
     * Compute a token associated to a login string.
     *
     * @param login the string.
     * @return the token string.
     */
    protected String getToken(String login) {

        return getToken(new User(login));
    }
    //__________________________________________________________________________
    /**
     * Checks whether the user's password is correct or not.
     *
     * @param usr the user's login.
     * @param pw  the user's password.
     * @return true if the password is correct, false otherwise.
     */
    protected boolean checkPassword(String usr, String pw) {

        if (usr == null || pw == null) {
            return false;
        }

        Iterator<Map.Entry<String, String>> it = accounts.iterator();

        for (; it.hasNext();) {

            Map.Entry<String, String> e = it.next();

            if (e.getKey().equals(usr)) {
                return e.getValue().equals(pw);
            }
        }

        return false;
    }
    //__________________________________________________________________________
    /**
     * Prints a description of this object on standard output.
     *
     * @param args the array of options.
     */
    public static void main(String[] args) {

        Options options = new Options();
        options.addOption("h", false, "help");
        options.addOption("d", false, "debug");
        options.addOption("b", true, "basic authentication");
        options.addOption("t", true, "token authentication");
        options.addOption("g", true, "get a token");
        options.addOption("D", false, "dump this");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        }
        catch (ParseException ex) {
            System.out.println(
            "Usage : java org.svom.vhfdb.server.Authorizer [-h]");
            System.err.println("Can't parse command line !");
            System.exit(1);
        }

        if (cmd.hasOption("h")) {
            System.out.println(
            "Usage : java org.svom.vhfdb.server.Authorizer [-h][-d]\n"
              + "-b login:pw | -t jwtoken | -g login | -D");

            System.exit(0);
        }

        Authorizer a = Authorizer.instance();

        if (cmd.hasOption("d")) {
            a.setDebug();
        }

        String str;
        User usr;

        if (cmd.hasOption("b")) {
            str = cmd.getOptionValue("b");
            str = Base64.getEncoder().encodeToString(str.getBytes());

            usr = a.basicCheck(str);
            System.out.println("Basic check: " + String.valueOf(usr));
        }
        else if (cmd.hasOption("t")) {
            str = cmd.getOptionValue("t");
            usr = a.tokenCheck(str);
            System.out.println("Token check: " + String.valueOf(usr));
        }
        else if (cmd.hasOption("g")) {
            str = cmd.getOptionValue("g");
            str = a.getToken(str);
            System.out.println(String.valueOf(str));
        }
        else if (cmd.hasOption("D")) {
            System.out.println("Authorizer: " + String.valueOf(a));
        }

     }
    //__________________________________________________________________________
}


