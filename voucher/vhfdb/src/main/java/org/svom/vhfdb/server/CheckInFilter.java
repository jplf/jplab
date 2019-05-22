//______________________________________________________________________________

//      Svom workbench - Voucher project
//______________________________________________________________________________

package org.svom.vhfdb.server;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.net.HttpURLConnection;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.svom.vhfdb.data.User;

/**
 * CheckInFilter checks incoming requests.
 *
 * @author Jean-Paul Le Fèvre
 */

public class CheckInFilter implements Filter {
    /**
     * Logger for this class and subclasses.
     */
    private Log logger = LogFactory.getLog("org.svom.vhfdb");
    /**
     * The configuration of this filter.
     */
    private FilterConfig filterConfig;
    /**
     * The realm to which this application belongs.
     */
    private String realm = "Svom";

    //__________________________________________________________________________
    /**
     * Filters what has to be filtered.
     *
     * It is used to check user's credential when authentication
     * is based on the Basic protocol.
     *
     * @param srequest the incoming request.
     * @param sresponse the outgoing response.
     * @param chain the rest of the filters to perform.
     * @throws IOException if something goes wrong.
     * @throws ServletException if something goes wrong.
     */
    public void doFilter(final ServletRequest srequest,
                         final ServletResponse sresponse,
                         FilterChain chain)
        throws IOException, ServletException {

        logger.info("Check In filter working ...");

        // The s prefixed paramq are for the non http ones.
        HttpServletRequest  request  = (HttpServletRequest) srequest;
        HttpServletResponse response = (HttpServletResponse) sresponse;

        // RFC 7230 : case-insensitive field name
        String header = request.getHeader("Authorization");

        if (header == null) {
            deny(response, "No credential provided");
            return;
        }

        String[] tokens = header.split(" ");
        StringBuilder buf = new StringBuilder("<:");
        for (int i = 0; i < tokens.length; i++) {
            buf.append(' ').append(String.valueOf(i));
            buf.append(' ').append(tokens[i]).append('>');
        }
        logger.debug("Authorization Header: " + buf.toString());

        Authorizer authorizer = Authorizer.instance();

        if (tokens.length < 2) {
            deny(response, "Invalid authentication header");
            return;
        }
        else if ("Basic".equalsIgnoreCase(tokens[0])) {

            // First verify the password associated to the login.
            User user = authorizer.basicCheck(tokens[1]);
            if (user == null) {
                deny(response, "Access denied: wrong password");
                return;
            }

            // Then compute a json web token and sent it back.
            String jwtoken = authorizer.getToken(user);
            response.setHeader("Token", jwtoken);

        }
        else if ("Bearer".equalsIgnoreCase(tokens[0])) {
            // Verify the token.
            User user = authorizer.tokenCheck(tokens[1]);
            if (user == null) {
                deny(response, "Access denied: wrong token");
                return;
            }
        }
        else {
            deny(response, "Invalid authentication scheme: " + tokens[0]);
            return;
        }

        chain.doFilter(srequest, sresponse);
    }
    //__________________________________________________________________________
    /**
     * Initializes this filter.
     * @param config the configuration
     */
    public void init(final FilterConfig config) {
        filterConfig = config;
    }
    //__________________________________________________________________________
    /**
     * Does nothing.
     */
    public void destroy() {                                           //5
    }
    //__________________________________________________________________________
    /**
     * Replies that access in denied.
     *
     * @param response the response
     * @param message an explanation
     * @throws IOException is something goes wrong.
     */
    private void deny(HttpServletResponse response, String message)
        throws IOException {

        response.setHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
        if (message == null) {
            message = "Access denied !";
        }

        response.sendError(HttpURLConnection.HTTP_UNAUTHORIZED, message);
    }
    //__________________________________________________________________________
}
