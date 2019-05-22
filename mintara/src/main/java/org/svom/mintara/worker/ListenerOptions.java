//______________________________________________________________________________

//      Svom  workbench - Mintara project

//______________________________________________________________________________

package org.svom.mintara.worker;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

/**
 * A option parser for the Listener.
 *
 * It parses and checks options for the Listener program.
 *
 * @see Listener
 * @see https://github.com/pcj/google-options
 * @author Jean-Paul Le Fèvre <jean-paul.lefevre@cea.fr>
 */

/**
 * The list of options available to this program.
 */
public class ListenerOptions extends OptionsBase {

    /**
     * The help message option.
     */
    @Option(
            name = "help",
            abbrev = 'h',
            help = "Prints usage info.",
            defaultValue = "false"
            )
            public boolean help;
    /**
     * The verbosity level.
     */
    @Option(
            name = "verbose",
            abbrev = 'v',
            help = "Output more details.",
            defaultValue = "false"
            )
            public boolean verbose;
     /**
     * The nats server url parameter.
     */
    @Option(
            name = "nats",
            abbrev = 'n',
            help = "Nats server URL",
            defaultValue = "nats://svom:mot2passe@localhost:4222"
            )
            public String natsUrl;
    /**
     * The nats cluster parameter.
     */
    @Option(
            name = "cluster",
            abbrev = 'C',
            help = "Nats cluster",
            defaultValue = "svom-cluster"
            )
            public String natsCluster;
     /**
     * The channels  parameter.
     */
    @Option(
            name = "channels",
            abbrev = 'c',
            help = "List of channels",
            defaultValue = "test"
            )
            public String channels;
     /**
     * The user parameter.
     */
    @Option(
            name = "user",
            abbrev = 'u',
            help = "User id",
            defaultValue = "svom"
            )
            public String user;
     /**
     * The password parameter.
     */
    @Option(
            name = "password",
            abbrev = 'p',
            help = "User's password",
            defaultValue = "mot2passe"
            )
            public String password;
}
//__________________________________________________________________________


