//______________________________________________________________________________

//      Svom  workbench - Voucher project
//______________________________________________________________________________

package org.svom.vhfdb.server;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

import java.util.Date;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

/**
 * Listener takes care of the vhfdb web application lifecycles.
 *
 * @author Jean-Paul Le Fèvre
 */

public class AppContextListener implements ServletContextListener {
  /**
   * @serial The flag raised when the context has been initialized.
   */
private static boolean initialized = false;
    /**
     * The Gregorian date format.
     */
private static final SimpleDateFormat DATE_FORMAT =
                                      new SimpleDateFormat("HH:mm:ss dd/MM");
    /**
     * Logger for this class and subclasses.
     */
private Log logger = LogFactory.getLog("org.svom.vhfdb");

    //__________________________________________________________________________
    /**
     * Initializes the context.
     *
     * @param ev the context event.
     */
    public void contextInitialized(ServletContextEvent ev) {
        ServletContext ctx = ev.getServletContext();

        String prefix = ctx.getRealPath("/");
        PropertyConfigurator.configure(prefix
                                       + "WEB-INF/classes/log4j.properties");

        if (initialized) {
            logger.error("VHF Db Application context initialized twice !");
            return;
        }

        logger.info("VHF Db Application context initialized !");
        logger.info(VersionInfo.instance().toString());

        initializeConnection(ctx);

        logger.info("VHF Db started at " + System.currentTimeMillis() + " ms. "
                    + DATE_FORMAT.format(new Date()));

        initialized = true;
    }
    //__________________________________________________________________________
    /**
     * Runs when the context is destroyed.
     * @param ev the context event.
     */
    public void contextDestroyed(ServletContextEvent ev) {
        logger.info("VHF Db destroyed   at "
                    + System.currentTimeMillis() + " ms."
                    + DATE_FORMAT.format(new Date()));
    }
    //__________________________________________________________________________
    /**
     * Initializes connections.
     *
     * @param ctx the servlet context.
     */
    private void initializeConnection(ServletContext ctx) {

        logger.info("Initializing connections  ...");

        logger.info("VHF Db connections ready.");
    }
    //__________________________________________________________________________
}
