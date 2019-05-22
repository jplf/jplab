//______________________________________________________________________________

//      Svom  workbench - Bunny project
//______________________________________________________________________________

package org.svom.bunny.worker;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP;

import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeoutException;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

/**
 * A very simple program to handle messages.
 *
 * Usage: java org.svom.bunny.worker.Worker [-h][-l][-s] worker_name [blah blah]
 *
 * This worker can produce and consume messages.
 *
 * A listening worker is cleanly stopped when it receives 'close'
 * as the content of a message.
 *
 * A worker broadcasts words given on the command line after its name.
 *
 * @see https://www.rabbitmq.com/tutorials/tutorial-one-java.html
 * @author Jean-Paul Le Fèvre <jean-paul.lefevre@cea.fr>
 */
public final class Worker {
    /**
     * The name of this worker.
     */
    private String workerName = "anonymous";
    /**
     * The connection.
     */
    private Connection connection;
    /**
     * The channel of communication.
     */
    private Channel channel;
    /**
     * The name of the message queue.
     */
    private String queueName;
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
     * @param args the array of command line arguments.
     * @throws IOException if something goes wrong.
     * @throws TimeoutException if something goes wrong.
     */
    private Worker(String[] args)  throws IOException, TimeoutException {

        Options options = new Options();
        options.addOption("h", false, "help");
        options.addOption("l", false, "listen to incoming messages");
        options.addOption("s", false, "send messages to the exchange");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        }
        catch (ParseException ex) {
            System.err.println("Can't parse command line !");
            printUsage();
            System.exit(1);
        }

        if (cmd.hasOption("h")) {
            printUsage();
            System.exit(0);
        }


        boolean listen = cmd.hasOption("l");

        boolean speak = cmd.hasOption("s");

        // Left-over arguments
        String[] cliArgs = cmd.getArgs();

        if (cliArgs != null && cliArgs.length > 0) {
            workerName = cliArgs[0];
        }

        doWork();

        if (speak) {
            if (cliArgs.length < 2) {
                System.out.println("What do you want to say ?");
                printUsage();
                System.exit(1);
            }
            doSpeak(cliArgs);
        }

        if (listen) {
            doListen();
        }

        System.out.println("Worker " + workerName + " created !");
    }
    //__________________________________________________________________________
    /**
     * Prints the accepted command line arguments.
     */
    private void printUsage() {
        System.out.println(
              "Usage : java org.svom.bunny.worker.Worker [-h][-s][-l] "
              + "name [blah blah]");
    }
    //__________________________________________________________________________
    /**
     * Initializes the object.
     * @throws IOException if something goes wrong.
     * @throws TimeoutException if something goes wrong.
     */
    private void doWork() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("hurukan");
        connection = factory.newConnection();
        channel = connection.createChannel();
        // Not durable, autodelete
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout", false, true, null);

        queueName = workerName + "_queue";
    }
    //__________________________________________________________________________
    /**
     * Sends messages to the world.
     *
     * It takes parameters given on the command line after the name
     *
     * @param words a list of words to say.
     * @throws IOException if something goes wrong.
     * @throws TimeoutException if something goes wrong.
     */
    private void doSpeak(String[] words) throws IOException, TimeoutException  {

        System.out.println("I'm gonna speak ...");

        for (int i = 1; i < words.length; i++) {

            String now = FMT.format(new Date());
            String message = now + " message content = " + words[i];
            System.out.println(message);

            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
        }

        channel.close();
        connection.close();
    }
    //__________________________________________________________________________
    /**
     * Listens and works.
     * @throws IOException if something goes wrong.
     */
    private void doListen() throws IOException {

        // Not durable, not exclusive, autoDelete
        queueName = channel.queueDeclare(queueName,
                                         false, false, true, null).getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println("Waiting for messages on " + queueName + " ...");

        Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body)
                    throws IOException {

                    System.out.println("Tag: " + consumerTag);
                    System.out.println("Envelope: " + envelope);

                    String message = new String(body, "UTF-8");
                    System.out.println("Message: " + message);

                    // Equal is used as a separator
                    int i = message.indexOf('=');
                    String content = "?";
                    if (i > 0) {
                        content = message.substring(i + 1).trim();
                    }
                    System.out.println("Content: " + content);

                    if ("close".equals(content)) {
                        System.out.println("Closing communications ...");
                        try {
                            channel.close();
                            connection.close();
                        }
                        catch (TimeoutException ex) {
                            System.err.println("Time out when closing !");
                            System.exit(1);
                        }
                    }
                }
            };

        channel.basicConsume(queueName, true, consumer);
     }
    //__________________________________________________________________________
    /**
     * Main method.
     * @param args the arguments of the program.
     */
    public static void main(String[] args) {

        try {
            Worker prog = new Worker(args);
        }
        catch (Exception ex) {
            System.err.println("Worker failed: " + ex);
            ex.printStackTrace();
        }
    }
    //__________________________________________________________________________
}
