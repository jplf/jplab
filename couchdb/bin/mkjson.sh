#!/bin/sh

# Load into the Couch database the content of the mp3 tags.
# First select wich disc directories to process, update the variable disc
# just below. This script creates json a json file for each disc.
# Finally run the script ldjson.sh to load the content into the database.

disc="77"

dest=$HOME/work/svomfsc/sandbox/couchdb/data
src=/mnt/freecom/jaypee/mp3
cmd=$HOME/work/xmm/config/bin/mp3tag.pl

for d in $disc
do
    echo "Processing $d ..."
    $cmd $src/$d/*.mp3 > $dest/$d.json
done

