#!/bin/sh

HOST='http://'$USER:$MY_PW'@localhost:5984'
curl -v -X PUT $HOST'/zikmu/_design/mp3' --data-binary @mp3.json
