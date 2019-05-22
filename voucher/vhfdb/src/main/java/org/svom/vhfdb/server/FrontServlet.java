//______________________________________________________________________________

//      Svom  workbench - Voucher project
//______________________________________________________________________________

package org.svom.vhfdb.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The front servlet first displayed to users.
 *
 * @author  Jean-Paul Le Fèvre
 */
public class FrontServlet extends BaseServlet {

//______________________________________________________________________________
/**
 * Handles the request.
 *
 * @param request contains the request the client has made of the servlet.
 * @param response contains the response the servlet sends to the client.
 * @throws IOException if an input or output error is detected.
 * @throws ServletException if the request could not be handled.
 */
protected void handleRequest(HttpServletRequest request,
                                      HttpServletResponse response)
    throws ServletException, IOException {

    logRequest(request, "front servlet");

    forwardResponse(request, response, "welcome.html");
}
//______________________________________________________________________________
}
