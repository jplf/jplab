#_______________________________________________________________________________

# Script SQL used to set the privileges.
# Usage : mysql -u root -p < setpriv.sql
# Jean-Paul Le Fèvre Janvier 1999
# setpriv,v 1.2 2000/03/02 16:46:06 lefevre Exp
#_______________________________________________________________________________

use mysql;

# Host User Passwd
# Select Insert Update Delete Create Drop Reload
# Shutdown Process File Grant References Index Alter

insert into user (Host, User, Password)
            values ('%', 'lefevre', password('mot2passe'));

insert into user (Host, User, Password)
            values ('%', 'guest', password('mot2passe'));

flush privileges;

# update user set host = '%'      where user = 'guest';
# update user set drop_priv = 'Y' where user = 'lefevre';
# update user set file_priv = 'Y' where user = 'lefevre';

#_______________________________________________________________________________
