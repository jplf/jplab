//______________________________________________________________________________

//      Svom Rest workbench
//______________________________________________________________________________

package org.svom.stations.webapp;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;
import java.util.Base64;
import java.util.StringTokenizer;

// import java.security.Principal;
// import javax.ws.rs.core.SecurityContext;

/**
 * The AccessFilter which implements authentication.
 *
 * This object is registered in the main class.
 *
 */
public final class AccessFilter implements ContainerRequestFilter {
    /**
     * The logger used to get messages.
     */
    private Logger logger = null;

    //__________________________________________________________________________
    /**
     * Creates the filter.
     */
    public AccessFilter() {

        logger = Logger.getLogger("org.svom.stations.webapp");
        logger.warning("AccessFilter created");
    }
    //__________________________________________________________________________
    /**
     * Filter method called before a request has been dispatched to a resource.
     *
     * This implementation just prints the incoming request header content.
     *
     * @param requestContext the context of the request.
     * @throws IOException if the filter fails.
     */
    public void filter(ContainerRequestContext requestContext)
        throws IOException {

        if (StationService.debug > 0) {
            MultivaluedMap<String, String> headers =
                requestContext.getHeaders();

            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                System.out.println(entry.getKey() + ": ");
                List<String> list = entry.getValue();
                for (String value : list) {
                    System.out.println("\t" + value);
                }
            }
        }

        String ident = requestContext.getHeaderString("authorization");
        if (ident == null) {
            requestContext.abortWith(Response
                                     .status(Response.Status.BAD_REQUEST)
                                     .entity("Missing credentials")
                                     .build());
            return;
        }

        ident = ident.replaceFirst("[Bb]asic ", "");
        try {
            byte[] value = Base64.getDecoder().decode(ident);
            ident = new String(value, "UTF-8");
        }
        catch (Exception ex) {
            logger.severe("Decoding " + ident + " failed");
            logger.severe("Reason " + ex.getMessage());
            requestContext.abortWith(Response
                                     .status(Response.Status.BAD_REQUEST)
                                     .entity("Internal filter error")
                                     .build());
            return;
        }

        StringTokenizer tokenizer = new StringTokenizer(ident, ":");
        String username = tokenizer.nextToken();
        String password = tokenizer.nextToken();

        logger.fine("Identity: " + String.valueOf(username)
                    + " (" + password + ")");

        if (!checkIdentity(username, password)) {
            requestContext.abortWith(Response
                                     .status(Response.Status.UNAUTHORIZED)
                                     .entity("Invalid credentials")
                                     .build());
            return;
        }

        System.out.println();
    }
    //__________________________________________________________________________
    /**
     * Checks if a credential is valid or not.
     *
     * @param login the login string.
     * @param pw the associated password
     * @return true if the login is accepted.
     */
    private boolean checkIdentity(String login, String pw) {

        if (login == null || pw == null) {
            return false;
        }

        if ("jaypee".equals(login) && "m2p".equals(pw)) {
            return true;
        }

        return false;
    }
    //__________________________________________________________________________
}

