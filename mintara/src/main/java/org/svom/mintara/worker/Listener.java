//______________________________________________________________________________

//      Svom  workbench - Mintara project

// See also : io.nats.streaming.examples.Subscriber
//______________________________________________________________________________

package org.svom.mintara.worker;

import io.nats.streaming.NatsStreaming;
import io.nats.streaming.Options;
import io.nats.streaming.StreamingConnection;
import io.nats.streaming.Subscription;
import io.nats.streaming.MessageHandler;
import io.nats.streaming.Message;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.Collections;

import com.google.devtools.common.options.OptionsParser;

/**
 * A listener to messages sent to the Nats server.
 *
 * It receives messages sent by any clients.
 *
 * Usage: java org.svom.mintara.worker.Listener
 * Or try the script <code>listener.sh</code>
 *
 * @see Greeter
 * @see https://github.com/nats-io/java-nats-streaming
 * @see http://javadoc.io/doc/jnats/2.4.1
 * @author Jean-Paul Le Fèvre <jean-paul.lefevre@cea.fr>
 */

public final class Listener {

    //__________________________________________________________________________
    /**
     * Creates the object.
     */
    private Listener() {

        System.out.println("Listener created !");
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
            OptionsParser.newOptionsParser(ListenerOptions.class);
        parser.parseAndExitUponError(args);
        
        ListenerOptions options = parser.getOptions(ListenerOptions.class);
        
        if (options.help) {
            printUsage(parser);
            return;
        }
        
        if (options.natsUrl.isEmpty() || options.user.isEmpty()
            || options.channels.isEmpty()) {
            printUsage(parser);
            return;
        }
        

        if (options.verbose) {
            StringBuilder str = new StringBuilder()
                .append("Url: ").append(options.natsUrl)
                .append(" Cluster: ").append(options.natsCluster)
                .append(" Channels: ").append(options.channels)
                .append(" User: ").append(options.user);
            
            System.out.println(str.toString());
        }
        
        Options natsOptions = new
            Options.Builder().natsUrl(options.natsUrl).build();
       
        StreamingConnection connection = null;
        try {
            connection = NatsStreaming.connect(options.natsCluster,
                                               options.user, natsOptions);
        }
        catch (InterruptedException ex) {
            System.out.println("Got an interruption " + ex);
            System.exit(1);
        }

        System.out.println("Waiting for messages ...");

        MessageHandler handler = new MessageHandler() {
                @Override
                public void onMessage(Message msg) {

                    byte[] payload = msg.getData();
                    String text = new String(payload);
                    
                    System.out.println("Got '" + text  + "'");
                    if ("kill".equals(text)) {
                        System.out.println("I'm gonna die ...");
                        System.exit(0);
                   }
                }
            };

        try {

            String[] channels = options.channels.split(",");
            System.out.println("Number of channels to follow : "
                               + channels.length
                               + " in " + options.channels);
            
            for (int i = 0; i < channels.length; i++) {
                String c = channels[i].trim();
                System.out.println("Subscription to " + c);
                Subscription subscription = connection.subscribe(c, handler);
            }
        }
        catch (InterruptedException ex) {
            System.out.println("Got " + ex);
        }
    }
   //__________________________________________________________________________
   /**
     * Prints how to use the program.
     * @param parser the option parser.
     */
     private static void printUsage(OptionsParser parser) {
        System.out.println(
        "Usage: java org.svom.mintara.worker.Listener options:");
        System.out.println(
               parser.describeOptions(Collections.<String, String>emptyMap(),
                                      OptionsParser.HelpVerbosity.LONG));
    }
   //__________________________________________________________________________
}

