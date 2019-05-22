#_______________________________________________________________________________

# Script SQL used to dump table user.
# Usage : showusers
# Jean-Paul Le Fèvre March 2000
# $Id: showusers.sh,v 1.3 2003/04/02 08:09:51 lefevre Exp $
#_______________________________________________________________________________

mysql -p --user=root  << _EOS_
USE mysql;
SELECT * FROM user;
_EOS_

#_______________________________________________________________________________
