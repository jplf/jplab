//______________________________________________________________________________

//      Svom  workbench - Mintara project

//______________________________________________________________________________

package org.svom.mintara.worker;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

/**
 * A option parser for Patron.
 *
 * It parses and checks options for the Patron program.
 *
 * @see Patron
 * @see https://github.com/pcj/google-options
 * @author Jean-Paul Le Fèvre <jean-paul.lefevre@cea.fr>
 */

/**
 * The list of options available to this program.
 */
public class PatronOptions extends OptionsBase {

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
     * The channel  parameter.
     */
    @Option(
            name = "channel",
            abbrev = 'c',
            help = "Messaging channel",
            defaultValue = "test"
            )
            public String channel;
     /**
     * The user parameter.
     */
    @Option(
            name = "user",
            abbrev = 'u',
            help = "User id",
            defaultValue = "patron"
            )
            public String user;
}
//__________________________________________________________________________


