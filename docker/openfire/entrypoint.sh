#!/bin/bash
# ___________________________________________________________________________

# Jean-Paul Le Fèvre - 5 October 2016
#
# The first time this script is run it uses the configuration coming
# with the downloaded package.
#
# Then it takes the copies found on the mounted volume and stored locally.
# ___________________________________________________________________________

set -e

cd ${OPENFIRE_HOME}

# The first time the conf is moved to the volume.
if [ ! -d  ${OPENFIRE_DATA_DIR}/conf ]; then
    mv conf ${OPENFIRE_DATA_DIR}
else
    rm -fr conf 
fi

# Use the file from the volume.
ln -sf ${OPENFIRE_DATA_DIR}/conf .

if [ ! -d  ${OPENFIRE_DATA_DIR}/logs ]; then
    mv logs ${OPENFIRE_DATA_DIR}
else
    rm -fr logs
fi

ln -sf ${OPENFIRE_DATA_DIR}/logs .

if [ ! -d  ${OPENFIRE_DATA_DIR}/embedded-db ]; then
    if [ -d  embedded-db ]; then
        mv embedded-db ${OPENFIRE_DATA_DIR}
    else
        mkdir ${OPENFIRE_DATA_DIR}/embedded-db
    fi
fi

ln -sf ${OPENFIRE_DATA_DIR}/embedded-db .

if [ -z ${1} ]; then
    # Start the programme and wait undefinitely.
    /opt/openfire/bin/openfirectl start
    tail -f /dev/null
else
    exec "$@"
fi

# ___________________________________________________________________________

