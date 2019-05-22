#!/bin/sh
#______________________________________________________________________________

# Script used to test the station services application.
# Check also with a browser or with the perl client.
# Jean-Paul Le Fèvre - June 2015
#______________________________________________________________________________

usage="Usage: client.sh [-h] -u user -m pw"
options="
[-i][-l|L][-n][-p file][-P file][-g name][-G name][-d name]"
readme="See also README.md"

# Select a plain or a TLS version.
# Look in Main.java to see which server is enabled.
root="http://localhost:8080/vhf/stations"
#root="https://localhost:8443/vhf/stations"

username=""
password=""
login=""

if [ -z "$1" ]; then
    echo $usage
    echo $options
    exit 1
fi

curl="curl -i -k --cacert etc/rest-server.pem"

while [ -n "$1" ]; do
case $1 in

    # Gets the number of stations. HEAD.
    -i)
            $curl $login -I "$root"
            echo
            shift
            ;;

    # Gets the deep list of stations. GET.
    -l)
            $curl $login "$root"
            echo
            shift
            ;;

    # Sets the username.
    -u)
            username=$2
            shift
            ;;

    # Sets the mot2passe.
    -m)
            password=$2
            login="-u $username:$password"
            shift
            ;;

    # Gets the shallow list of stations. GET.
    -L)
            $curl $login "$root?expand=false"
            echo
            shift
            ;;

    # Gets the list of station name.
    -n)
            $curl $login "$root?fields=name"
            echo
            shift
            ;;

    # Updates (PUT) a specific station. See also -p
    -P)
            if [ -z "$2" ]; then
                echo "Filename missing"
                exit 1
            elif [ ! -f $2 ]; then
                echo "File not found: $2"
                exit 1
            else 
                # The quoted name should be equals to the name in the file.
                name=`fgrep -m 1 "name" $2 |  awk 'BEGIN {FS=",| "};{print $2}'`
                uri=$root"/"$name
                
                $curl -H "Accept: application/json"\
                      -H "Content-type: application/json"\
                      -X PUT -d @$2 $login $uri
                echo
                shift
            fi
            shift
            ;;

    # Gets a station named. GET.
    -g)
            if [ -z "$2" ]; then
                echo "Name missing"
                exit 1
            else
                $curl $login "$root/$2"
                echo
                shift
            fi
            shift
            ;;

    # Gets a station named. GET with test on content type.
    -G)
            if [ -z "$2" ]; then
                echo "Name missing"
                exit 1
            else
                # "Accept: application/json" 200 OK
                # "Accept: text/html"        200 OK
                # "Accept: text/plain"       406 Not Acceptable
                $curl -v -H "Accept: text/plain" $login "$root/$2"
                echo
                shift
            fi
            shift
            ;;

    # Deletes a station named. DELETE.
    -d)
            if [ -z "$2" ]; then
                echo "Name missing"
                exit 1
            else
                $curl $login -X DELETE "$root/$2"
                echo
                shift
            fi
            shift
            ;;

    # Posts a new station. POST.
    -p)
            if [ -z "$2" ]; then
                echo "Filename missing"
                exit 1
            elif [ ! -f $2 ]; then
                echo "File not found: $2"
                exit 1
            else 
                $curl   -H "Accept: application/json"\
                        -H "Content-type: application/json"\
                        -X POST -d @$2 $login $root
                echo
                shift
            fi
            shift
            ;;

    -h|-?)
            echo $usage
            echo $options
            echo $readme
            exit 0
            ;;

    *)
            echo $usage
            echo $options
            exit 1
            ;;
esac
shift
done

#______________________________________________________________________________
