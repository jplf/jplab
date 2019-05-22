#! /bin/sh
#______________________________________________________________________________

# Set postgres admin password.

if [ -z "$1" ]; then
    echo "Usage : ./config.sh pw"
    exit 1
fi

docker secret rm postgres_password
echo -n $1 | docker secret create -l content="PostgresQL root password" \
                    postgres_password -

#______________________________________________________________________________
