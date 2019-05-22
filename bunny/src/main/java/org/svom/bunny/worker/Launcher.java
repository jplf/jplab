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
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeoutException;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

/**
 * A very simple program wrapper used to launch programs.
 *
 * Usage:
 * java org.svom.bunny.worker.Launcher [-h][-v] -n launcher_name prog args ...
 *
 * This wrapper starts a program when it is ordered to do so, sends a message
 * when the program completes.
 *
 * @author Jean-Paul Le Fèvre <jean-paul.lefevre@cea.fr>
 */
public final class Launcher {
    /**
     * The name of this worker.
     */
    private String launcherName;
    /**
     * The verbosity.
     */
    private boolean verbose = false;
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
     * The list of accepted orders.
     */
    private static final String ORDERS = "start, close";
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
    private Launcher(String[] args) throws IOException, TimeoutException {

        Options options = new Options();
        options.addOption("h", false, "help");
        options.addOption("v", false, "verbose");
        options.addOption("n", true, "name of this launcher");

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

        if (cmd.hasOption("v")) {
            verbose = true;
        }

        launcherName = cmd.getOptionValue("n");
        if (launcherName == null) {
            System.out.println("Name of this launcher is missing !");
            printUsage();
            System.exit(1);
        }

        // Left-over arguments - Must be quoted.
        String[] leftArgs = cmd.getArgs();

        if (leftArgs == null || leftArgs.length < 1) {
            System.out.println("No command to launch specified !");
            printUsage();
            System.exit(1);
        }

        String command = leftArgs[0];

        if (verbose) {
            System.out.println("Handling " + command + " ...");
        }

        // Initalize the messaging routines.
        getReady();

        if (verbose) {
            System.out.println("Launcher " + launcherName + " created !");
        }

        // Wait for signal then start th command
        prepareCommand(command);
    }
    //__________________________________________________________________________
    /**
     * Prints the accepted command line arguments.
     */
    private void printUsage() {
        System.out.println(
              "Usage : java org.svom.bunny.worker.Launcher [-h][-v] -n name "
              + "\"program args ...\"");
    }
    //__________________________________________________________________________
    /**
     * Initializes the connections.
     * @throws IOException if something goes wrong.
     * @throws TimeoutException if something goes wrong.
     */
    private void getReady() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("hurukan");
        connection = factory.newConnection();
        channel = connection.createChannel();
        // Not durable, autodelete
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout", false, true, null);

        queueName = launcherName + "_queue";
    }
    //__________________________________________________________________________
    /**
     * Gets ready to launch a command after message received.
     * @param command the array of arguments
     * @throws IOException if something goes wrong.
     */
    private void prepareCommand(String command) throws IOException {

        // Not durable, not exclusive, autoDelete
        queueName = channel.queueDeclare(queueName,
                                         false, false, true, null).getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        if (verbose) {
            System.out.println("Waiting for message on " + queueName + " ...");
        }

        Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body)
                    throws IOException {

                    String message = new String(body, "UTF-8");

                    if (verbose) {
                        System.out.println("Tag: " + consumerTag);
                        System.out.println("Envelope: " + envelope);
                        System.out.println("Message: " + message);
                    }

                    // Equal is used as a separator
                    int i = message.indexOf('=');
                    String order = null;
                    if (i < 1) {
                        System.out.println("Invalid message: " + message);
                        return;
                    }

                    order = message.substring(i + 1).trim();
                    if (verbose) {
                        System.out.println("Order: " + order);
                    }

                    if ("close".equals(order)) {
                        if (verbose) {
                            System.out.println("Closing communications ...");
                        }
                        try {
                            channel.close();
                            connection.close();
                        }
                        catch (TimeoutException ex) {
                            System.err.println("Time out when closing !");
                            System.exit(1);
                        }
                    }
                    else if ("start".equals(order)) {
                        int status = doLaunchCommand(command);
                        String report = "status: " + status;
                        reportStatus(report);
                    }
                    else if (!order.startsWith("status")) {
                        System.out.println("Unkown order: " + order
                                           + " (" + ORDERS + ")");
                        return;
                    }
                }
            };

        channel.basicConsume(queueName, true, consumer);
     }
    //__________________________________________________________________________
    /**
     * Starts execution of the command.
     *
     * @param command the program and its arguments.
     * @return the exit status of the command.
     */
    private int doLaunchCommand(String command) {

        System.out.println("Launching: " + command);

        ProcessBuilder pb = new ProcessBuilder(command.split(" "));

        File log = new File("processor.log");

        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(log));

        int status = -1;

        try {
            Process process = pb.start();

            status = process.waitFor();
            System.out.println("Command: " + command
                               + " [exit status: " + status + "]");
        }
        catch (IOException ex) {
            System.err.println("Command " + command + " failed: " + ex);
            if (verbose) {
                ex.printStackTrace();
            }
        }
        catch (InterruptedException ex) {
            System.err.println("Command " + command + " was interrupted: "
                               + ex);
            if (verbose) {
                ex.printStackTrace();
            }
        }

        return status;
    }
    //__________________________________________________________________________
    /**
     * Gets the program and it arguments.
     *
     * @param args the list of arguments the program to execute.
     * @return the name and the arguments in a string string.
     */
    private String getCommandLine(String[] args) {

        StringBuilder buf = new StringBuilder(args[0]);

        for (int i = 1; i < args.length; i++) {
            buf.append(' ').append(args[i]);
        }

        return buf.toString();
    }
    //__________________________________________________________________________
    /**
     * Sends a message to the boss.
     *
     * @param report what to say.
     * @throws IOException if something goes wrong.
     */
    private void reportStatus(String report) throws IOException {

        String now = FMT.format(new Date());
        String message = now + " message content = " + report;
        if (verbose) {
            System.out.println(message);
        }

        try {
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
        }
        catch (Exception ex) {
             System.err.println("Status report publication failed !");
        }
     }
    //__________________________________________________________________________
    /**
     * Main method.
     * @param args the arguments of the program.
     */
    public static void main(String[] args) {

        try {
            Launcher prog = new Launcher(args);
        }
        catch (Exception ex) {
            System.err.println("Launcher failed: " + ex);
            ex.printStackTrace();
        }
    }
    //__________________________________________________________________________
}
