#! /bin/sh
#______________________________________________________________________________

if [ -z "$1" ]; then
    echo "Usage : ./config.sh pw"
    exit 1
fi

docker secret rm mariadb_password
echo -n $1 | docker secret create -l content="MariadDb root password" \
                    mariadb_password -

#______________________________________________________________________________
