#! /usr/bin/perl
#______________________________________________________________________________

# Perl	Jean-Paul Le Fèvre - January 2003
# It is used to generate a timetable for the bus lines.
# It is based on the DBI perl modules and the Msql-Mysql-modules driver.
# $Id: mktimetable.pl,v 1.1 2003/01/23 11:14:21 lefevre Exp $
#______________________________________________________________________________

use DBI;

# First, try to connect to the database and to get back a handle.

my $database = "jvs_database";
my $hostname = "localhost";
my $user     = "lefevre";

my $dsn = "DBI:mysql:database=$database;host=$hostname;"
        . "mysql_read_default_file=/home/$user/.my.cnf";
my $dbh = DBI->connect($dsn, $user);

# Then, prints content of the bus_lines table.
# It is just to check that the connection is established and
# that the database is available.

my $table = "bus_lines";
my $sth   = $dbh->prepare("select * from $table");
my $rv    = $sth->execute;
print "Found $rv rows in $table\n";

while ( @row = $sth->fetchrow_array ) {
    print "@row\n";
}

# Now get ready to update the timetables. The list of lines and stops
# is fetched into the table bus_line_stops.

$sth    = $dbh->prepare("select line_id, stop_id from bus_line_stops");
$rv     = $sth->execute;
print "Found $rv rows in bus_line_stops\n";

# Insert information into bus_timetables.
my $str = "insert into bus_timetables (line_id, stop_id, trip_id, arrival)"
        . "values (?, ?, ?, ?)";

my $sti   = $dbh->prepare($str);
my $trip  = 1;
my $line  = -1;
my $arrival;

# A very simple procedure. Only one trip is considered. Arrival time
# is just computed by incrementing a value. The purpose is to get a
# non empty timetables table.

while ( @row = $sth->fetchrow_array ) {

    if($line != $row[0]) { # A new line, reset time.
	$arrival = 7;
	$line    = $row[0];
    }
    else {
	$arrival++;
    }
    $str = sprintf "%d:00:00", $arrival;

    $rv = $sti->execute($row[0], $row[1], $trip, $str);
    print "@row returns $rv $str\n";
}

# Disconnect from my sql db.

$rc = $dbh->disconnect  || warn $dbh->errstr;
1;

#______________________________________________________________________________



