//______________________________________________________________________________

//      Svom  workbench - Bunny project
//______________________________________________________________________________

package org.svom.bunny.worker;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.concurrent.TimeoutException;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * A very simple message producer.
 *
 * It is based on a 'fanout' configuration. Message can be consumed by
 * a listening worke or by a patron.
 *
 * Usage: java org.svom.bunny.worker.Greeter [some_id]
 * Enter 'q' or 'x' to exit the loop.
 *
 * @see https://www.rabbitmq.com/tutorials/tutorial-one-java.html
 * @author Jean-Paul Le Fèvre <jean-paul.lefevre@cea.fr>
 */
public final class Greeter {
     /**
     * The name of the exchange.
     */
    private static final String EXCHANGE_NAME = "bunny";
    /**
     * A format to generate timestamps.
     */
    private static final SimpleDateFormat FMT =
        new SimpleDateFormat("HH:mm:ss");

    //__________________________________________________________________________
    /**
     * Creates the object.
     */
    private Greeter() {

        System.out.println("Greeter created !");
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

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("hurukan");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // Not durable, autodelete
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout", false, true, null);

        String id = "anonymous";
        if (args.length > 0) {
            id = args[0];
        }

        BufferedReader d =
            new BufferedReader(new InputStreamReader(System.in));

        int count = 0;

        System.out.println("Enter a message. To stop give 'x' or 'q' !");

        while (true) {

            System.out.print("> ");
            String line = d.readLine();

            if (line == null || "q".equals(line) || "x".equals(line)) {
                break;
            }

            // The = character is used as a separator
            if (line.indexOf('=') >= 0) {
                System.out.println("Invalid character '=' !");
                continue;
            }

            String now = FMT.format(new Date());
            String message = now + " message " + count++ + " from " + id;
            message += " = " + line;

            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
            System.out.println("Said '" + message + "'");
        }

        channel.close();
        connection.close();
    }
    //__________________________________________________________________________
}

