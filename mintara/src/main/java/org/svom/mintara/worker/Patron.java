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
 * A very simple message consumer.
 *
 * It receives messages sent by a greeter or a worker.
 *
 * Usage: java org.svom.mintara.worker.Patron
 * Or try the script <code>patron.sh</code>
 * If the string <i>kill</i> is received the program stops.
 *
 * @see Greeter
 * @see https://github.com/nats-io/java-nats-streaming
 * @see http://javadoc.io/doc/io.nats/java-nats-streaming/2.1.3
 * @author Jean-Paul Le Fèvre <jean-paul.lefevre@cea.fr>
 */

public final class Patron {

    //__________________________________________________________________________
    /**
     * Creates the object.
     */
    private Patron() {

        System.out.println("Patron created !");
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
            OptionsParser.newOptionsParser(PatronOptions.class);
        parser.parseAndExitUponError(args);
        
        PatronOptions options = parser.getOptions(PatronOptions.class);
        
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
            Subscription subscription = connection.subscribe(options.channel,
                                                             handler);
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
        "Usage: java org.svom.mintara.worker.Patron options:");
        System.out.println(
               parser.describeOptions(Collections.<String, String>emptyMap(),
                                      OptionsParser.HelpVerbosity.LONG));
    }
   //__________________________________________________________________________
}

