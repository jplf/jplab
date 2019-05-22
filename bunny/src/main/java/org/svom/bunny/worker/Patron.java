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
import java.util.concurrent.TimeoutException;

/**
 * A very simple message consumer.
 *
 * It receives messages sent by a greeter or a worker.
 *
 * Usage: java org.svom.bunny.worker.Patron
 *
 * @see Greeter
 * @see https://www.rabbitmq.com/tutorials/tutorial-one-java.html
 * @author Jean-Paul Le Fèvre <jean-paul.lefevre@cea.fr>
 */
public final class Patron {
    /**
     * The name of the exchange.
     */
    private static final String EXCHANGE_NAME = "bunny";
     /**
     * The name of the message queue.
     */
    private static final String QUEUE_NAME = "patron_queue";

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

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("hurukan");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout", false, true, null);

        // Not durable, not exclusive, autoDelete
        String str = channel.queueDeclare(QUEUE_NAME,
                                          false, false, true, null).getQueue();
        channel.queueBind(str, EXCHANGE_NAME, "");

        System.out.println("Waiting for messages on " + str + " ...");

        Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body)
                    throws IOException {

                    String message = new String(body, "UTF-8");
                    System.out.println("Got '" + message + "'");
                }
            };

        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
    //__________________________________________________________________________
}

