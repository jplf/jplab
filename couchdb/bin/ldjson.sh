#!/bin/sh

# First create the json input files with mkjson.sh
# Then update the disc variable below and run this script to load
# the content of the mp3 tags.

disc="77"

src=$HOME/work/svomfsc/sandbox/couchdb/data
cmd="node load.js"

for d in $disc
do
    echo "Processing $d ..."
    $cmd $src/$d.json
done

