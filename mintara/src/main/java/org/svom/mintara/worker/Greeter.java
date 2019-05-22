//______________________________________________________________________________

//      Svom  workbench - Mintara project

// See also : io.nats.streaming.examples.Publisher
//______________________________________________________________________________

package org.svom.mintara.worker;

import io.nats.streaming.StreamingConnection;
import io.nats.streaming.NatsStreaming;
import io.nats.streaming.Options;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.concurrent.TimeoutException;
import java.util.Date;
import java.util.Collections;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;

import com.google.devtools.common.options.OptionsParser;

/**
 * A very simple message producer.
 *
 * Usage: java org.svom.mintara.worker.Greeter [some_id]
 * Enter 'q' or 'x' to exit the loop.
 * Enter 'k' to kill the patron.
 * Rather try the script <code>greeter.sh</code>
 *
 * @see Patron
 * @see https://github.com/nats-io/java-nats-streaming
 * @see http://javadoc.io/doc/io.nats/java-nats-streaming/2.1.3
 * @author Jean-Paul Le Fèvre <jean-paul.lefevre@cea.fr>
 */
public final class Greeter {

    //__________________________________________________________________________
    /**
     * Creates the object.
     */
    private Greeter() {

        logMsg("Greeter created !");
    }
    //__________________________________________________________________________
    /**
     * Main method.
     *
     * @param args the arguments of the program.
     * @throws IOException if something goes wrong.
     * @throws TimeoutException if something goes wrong.
     */
    public static void main(String[] args)
        throws IOException, TimeoutException {
        
        OptionsParser parser =
            OptionsParser.newOptionsParser(GreeterOptions.class);
        parser.parseAndExitUponError(args);
        
        GreeterOptions options = parser.getOptions(GreeterOptions.class);
        
        if (options.help) {
            printUsage(parser);
            return;
        }
        
        if (options.natsUrl.isEmpty() || options.user.isEmpty()
            || options.channel.isEmpty()) {
            printUsage(parser);
            return;
        }
 
        if (options.verbose) {
            StringBuilder str = new StringBuilder()
                .append("Url: ").append(options.natsUrl)
                .append(" Cluster: ").append(options.natsCluster)
                .append(" Channel: ").append(options.channel)
                .append(" User: ").append(options.user);
            
            logMsg(str.toString());
        }
        
        Options natsOtions = new
            Options.Builder().natsUrl(options.natsUrl).build();
        StreamingConnection connection = null;

        try {
            connection = NatsStreaming.connect(options.natsCluster,
                                               options.user, natsOtions);
        }
        catch (InterruptedException ex) {
            logMsg("Got an interruption " + ex);
            Thread.currentThread().interrupt(); // Sonarqubination
            System.exit(1);
        }

        BufferedReader d =
            new BufferedReader(new InputStreamReader(System.in));

        int count = 0;

        logMsg("Enter a message. To stop give 'x' or 'q' !");
        logMsg("To kill the listener give 'k' !");

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        while (true) {

            logMsg(" ");
            String line = d.readLine();

            if (line == null || "q".equals(line) || "x".equals(line)) {
                break;
            }

            // The = character is used as a separator
            if (line.indexOf('=') >= 0) {
                logMsg("Invalid character '=' !");
                continue;
            }

            String now = dateFormat.format(new Date());
            String message = null;
            
            if ("k".equals(line)) {
                // Stop the listener.
                message = "kill";
            }
            else {
                message = now + " message " + count++ + " from " + options.user;
                message += " = " + line;
            }

            try {
                connection.publish(options.channel, message.getBytes());
            }
            catch (InterruptedException ex) {
                logMsg("Got " + ex);
                Thread.currentThread().interrupt(); // Sonarqubination
            }
 
            logMsg("Said '" + message + "'");
        }
        try {
            connection.close();
        }
        catch (InterruptedException ex) {
            logMsg("Got " + ex);
            Thread.currentThread().interrupt(); // Sonarqubination
        }
     }
    //__________________________________________________________________________
    //__________________________________________________________________________
   /**
     * Prints how to use the program.
     * @param parser the option parser.
     */
     private static void printUsage(OptionsParser parser) {
        logMsg(
        "Usage: java org.svom.mintara.worker.Greeter options:");
        logMsg(
               parser.describeOptions(Collections.<String, String>emptyMap(),
                                      OptionsParser.HelpVerbosity.LONG));
    }
    //__________________________________________________________________________
   /**
     * Prints a message to stdout.
     * Just to see if it makes sonarqube happier
     * @param msg the string to print.
     */
     private static void logMsg(String msg) {
         System.out.println(msg);
    }
   //__________________________________________________________________________
}

