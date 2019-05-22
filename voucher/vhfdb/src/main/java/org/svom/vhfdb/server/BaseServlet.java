//______________________________________________________________________________

//      Svom  workbench - Voucher project
//______________________________________________________________________________

package org.svom.vhfdb.server;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The base servlet used by this application.
 *
 * @author  Jean-Paul Le Fèvre
 */
public abstract class BaseServlet extends HttpServlet {
    /**
     * Logger for this class.
     */
    private Log logger = LogFactory.getLog("org.svom.vhfdb");

//______________________________________________________________________________
/**
 * Handles the HTTP GET method.
 *
 * This implementation calls the common handler.
 *
 * @param request contains the request the client has made of the servlet.
 * @param response contains the response the servlet sends to the client.
 * @throws IOException if an input or output error is detected.
 * @throws ServletException if the request for the GET could not be handled.
 * @see #handleRequest
 */
public void doGet(HttpServletRequest request, HttpServletResponse response)
                  throws ServletException, IOException {
    handleRequest(request, response);
}
//______________________________________________________________________________
/**
 * Handles the HTTP POST method.
 *
 * This implementation calls the common handler.
 *
 * @param request contains the request the client has made of the servlet.
 * @param response contains the response the servlet sends to the client.
 * @throws IOException if an input or output error is detected.
 * @throws ServletException if the request could not be handled.
 * @see #handleRequest
 */
public void doPost(HttpServletRequest request, HttpServletResponse response)
                   throws ServletException, IOException {
    handleRequest(request, response);
}
//______________________________________________________________________________
/**
 * Handles the request.
 *
 * @param request contains the request the client has made of the servlet.
 * @param response contains the response the servlet sends to the client.
 * @throws IOException if an input or output error is detected.
 * @throws ServletException if the request could not be handled.
 */
protected abstract void handleRequest(HttpServletRequest request,
                                      HttpServletResponse response)
                                      throws ServletException, IOException;
//______________________________________________________________________________
/**
 * Sends the reponse.
 *
 * @param request contains the request the client has made of the servlet.
 * @param response contains the response the servlet sends to the client.
 * @param page the jsp file presenting the response.
 * @throws IOException if an input or output error is detected.
 */
protected void forwardResponse(HttpServletRequest request,
                               HttpServletResponse response, String page)
                               throws IOException {
    try {
        RequestDispatcher rd =
        getServletConfig().getServletContext().getRequestDispatcher('/' + page);

        rd.forward(request, response);
    }
    catch (Exception ex) {
        ex.printStackTrace();
    }
}
//______________________________________________________________________________
/**
 * Logs the request.
 *
 * @param rq contains the request the client has made of the servlet.
 * @param message a remark made.
 */
    protected void logRequest(HttpServletRequest rq, String message) {

    StringBuilder buf = new StringBuilder("Request");

    String str = rq.getRemoteAddr();
    if (str != null) {
        buf.append(" from ").append(str);
    }

    if (message != null) {
        buf.append(" : ").append(message);
    }

    logger.info(buf.toString());
}
//______________________________________________________________________________
/**
 * Prints the content of a session.
 * <br>
 * @param session the session to examine.
 */
protected void dumpSession(HttpSession session) {

    StringBuilder buf = new StringBuilder("\n\nSession ");
    buf.append(session.getId()).append("\n");

    for (Enumeration e = session.getAttributeNames(); e.hasMoreElements();) {
        String a = (String) e.nextElement();
        Object v =  session.getAttribute(a);
        buf.append(a).append(" : ").append(String.valueOf(v)).append("\n");
    }
    buf.append("\n");

    logger.info(buf.toString());
}
//______________________________________________________________________________
/**
 * Prints the content of a request.
 * <br>
 * @param request the request to examine.
 */
protected void dumpRequest(HttpServletRequest request) {

    logger.info("\n");
    for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
        String param    = (String) e.nextElement();
        String[] values = request.getParameterValues(param);

        StringBuilder buf = new StringBuilder(param);
        buf.append(" :");

        for (int i = 0; i < values.length; i++) {
            buf.append(" '").append(values[i]);
        }
        buf.append("'");
        logger.info(buf.toString());
    }

    Enumeration e = request.getHeaderNames();
    StringBuilder buf = new StringBuilder();

    if (e.hasMoreElements()) {
        buf.append("\nHeaders :\n");
    }

    for (; e.hasMoreElements();) {
        String h = (String) e.nextElement();
        buf.append(h).append(" : ").append(request.getHeader(h)).append("\n");
    }

    logger.info(buf.toString());
}
//______________________________________________________________________________
/**
 * Returns the parameter value.
 *
 * If the parameter is not set or if it is white returns null.
 * @param request contains the request the client has made to the servlet.
 * @param name the name of the parameter.
 * @return the value without spaces.
 */
protected final String getParameter(HttpServletRequest request, String name) {

   String value = request.getParameter(name);
   if (value == null) {
       return null;
   }

   value = value.trim();

   return (value.length() > 0) ? value : null;
}
//______________________________________________________________________________
}
