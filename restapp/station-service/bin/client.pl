#!/usr/bin/perl
#_____________________________________________________________________________

# Perl     : A rest client in Perl for the station service.
# See also : client.sh
# Usage    : client.pl [-h][-d][-s][-u user][-p password][station_name]

# Author   : Jean-Paul Le Fèvre - August 2015
#_____________________________________________________________________________

use REST::Client;
use MIME::Base64;
use Data::Dumper;

#  Default values.
my $debug = 0;
my $ssl   = 1;
my $name  = "first";
my $user;
my $password;

# Get values from the command line.
&getArg();

my $headers = {Accept => 'application/json',
               Authorization => 'Basic '
               . encode_base64($user . ':' . $password)};


# Prepare statistics on the distribution.

($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);

if ($debug) {
    print Dumper($headers);
    printf "# Date   : %02d/%02d/%02d\n", $mday, $mon+1, $year-100;
}

# To avoid server checking.
# $ENV{PERL_LWP_SSL_VERIFY_HOSTNAME}=0;

my $client = REST::Client->new();
my $root = "http://localhost:8080/vhf/stations";

if ($ssl) {
    $root = "https://localhost:8443/vhf/stations";  
    $client->setCa('etc/rest-server.pem');
}

# Prints the number of stations
if ($debug) {
    $client->HEAD($root);
    print 'Count: ' . $client->responseHeader('total-count') . "\n";
    print "\n";
}

# Prints the description of a specific station
$client->GET($root . '/' . $name, $headers);

print 'Code: ' . $client->responseCode()  . "\n";

if ($debug) {
    foreach ( $client->responseHeaders() ) {
        print 'Header: ' . $_ . '=' . $client->responseHeader($_) . "\n";
    }
}

print $client->responseContent() . "\n";

print "\n";

#_____________________________________________________________________________

# Parses the command line

sub getArg {

    while(@ARGV && ($_ = $ARGV[0]) =~ /^-(.)(.*)/) {

        if($ARGV[0] eq "-d") {
            $debug = 1;
        }
        elsif($ARGV[0] eq "-s") {
            $ssl = ! $ssl;
        }
        elsif($ARGV[0] eq "-u") {
            $user = $ARGV[1];
            shift(@ARGV);
        }
        elsif($ARGV[0] eq "-p") {
            $password = $ARGV[1];
            shift(@ARGV);
        }
        elsif($ARGV[0] eq "-g") {
            $name = $ARGV[1];
            shift(@ARGV);
        }
        elsif($ARGV[0] eq "-h") {
            printf
            "Usage: client.pl " .
            "[-h][-d][-s][-u user][-p password][station_name]\n";
            exit;
        }
        shift(@ARGV);
    }
    1;
}
#______________________________________________________________________________

