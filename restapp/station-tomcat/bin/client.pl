#!/usr/bin/perl
#_____________________________________________________________________________

# Perl     : A rest client in Perl for the station service.
# See also : client.sh
# Usage    : client.pl [-h][-d][-s][-D][-u user][-m password][station_name]
#            -d : debug
#            -s : toggle ssl option
#            -D : swagger doc
# Environment : MY_LOGIN="name:password"

# Author   : Jean-Paul Le Fèvre - August 2015
#_____________________________________________________________________________

use REST::Client;
use MIME::Base64;
use Data::Dumper;
use JSON;

#  Default values.
my $debug = 0;
my $ssl   = 1;
my $doc   = 0;
my $name  = "first";

my $username = "";
my $password = "";
my $login    = $ENV{"MY_LOGIN"};

# Get values from the command line.
&getArg();

if ($username ne "" && $password ne "") {
    $login = $username . ':' . $password;
}

if ($login eq "") {
    print "Credential : -u username -m password ($login) \n";
    exit;
}

my $headers = {Accept => 'application/json',
               Authorization => 'Basic '
               . encode_base64($login)};


# Prepare statistics on the distribution.

($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);

if ($debug) {
    print Dumper($headers);
    printf "# Date   : %02d/%02d/%02d\n", $mday, $mon+1, $year-100;
}

# To avoid server checking.
# $ENV{PERL_LWP_SSL_VERIFY_HOSTNAME}=0;
my $json = JSON->new->allow_nonref;

my $client = REST::Client->new();
my $root = "http://localhost:8080/vhf/stations";

if ($ssl) {
    $root = "https://localhost:8443/vhf/stations";  
    $client->setCa($ENV{"SRV_HOME"} . '/etc/rest-server.pem');
}

# Prints the number of stations
if ($debug) {
    $client->HEAD($root);
    print 'Count: ' . $client->responseHeader('total-count') . "\n";
    print "\n";
}

if ($doc) {
    $root = "https://localhost:8443/vhf/stations/swagger.json";
    $client->GET($root, $headers);
}
else {
# Prints the description of a specific station
    $client->GET($root . '/' . $name, $headers);
}

if ($debug) {
    foreach ( $client->responseHeaders() ) {
        print 'Header: ' . $_ . '=' . $client->responseHeader($_) . "\n";
    }
}

if ($doc) {
    my $swagger = $json->decode($client->responseContent());
    print $json->pretty->encode($swagger) . "\n";
}
else {
    print 'Code: ' . $client->responseCode()  . "\n";
    print $client->responseContent() . "\n";
}

print "\n";

#_____________________________________________________________________________

# Parses the command line

sub getArg {

    while(@ARGV && ($_ = $ARGV[0]) =~ /^-(.)(.*)/) {

        if($ARGV[0] eq "-d") {
            $debug = 1;
        }
        elsif($ARGV[0] eq "-D") {
            $doc = 1;
        }
        elsif($ARGV[0] eq "-s") {
            $ssl = ! $ssl;
        }
        elsif($ARGV[0] eq "-u") {
            $username = $ARGV[1];
            shift(@ARGV);
        }
        elsif($ARGV[0] eq "-m") {
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
            "[-h][-d][-s][-u username][-m password][station_name]\n";
            exit;
        }
        shift(@ARGV);
    }
    1;
}
#______________________________________________________________________________

