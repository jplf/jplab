#!/bin/sh
#______________________________________________________________________________

# Script used to test the authentication mechanisms.
# Check also with other clients.
# Jean-Paul Le Fèvre - September 2016
#______________________________________________________________________________

usage="Usage: client.sh [-h] -u user -m pw"

target="http://localhost:8800/vhfdb/login"

username=""
password=""
login_string=""

if [ -z "$1" ]; then
    echo $usage
    echo $options
    exit 1
fi

# -i : include the HTTP-header in the  output.

curl="curl -i"

while [ -n "$1" ]; do
case $1 in

    # Sets the username.
    -u)
        username=$2
        shift
        ;;

    # Sets the mot2passe.
    -m)
        password=$2
        login_string="-u $username:$password"
        shift
        ;;

    # Updates (PUT) a specific station. See also -p
    -P)
        $curl -H "Accept: application/json"\
              -H "Content-type: application/json"\
              -X PUT -d @$2 $login_string $uri
        echo
        shift

        shift
        ;;
    
    -h|-?)
       echo $usage
       exit 0
       ;;

    *)
        echo $usage
        exit 1
        ;;
esac
shift
done

echo "First basic request !"
token=`$curl $login_string "$target" | grep "Token: " | sed 's/Token: //'`

echo
echo
echo "Second request with a token !"
$curl -H "Authorization: Bearer $token" "$target" 2>/dev/null

# Corrupt the token
token="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsZWZldnJlIn0.k03KvaIGvckSuHWq0e0KB-JY_wI6uAg0xYYrexeIek7K3zLcppkePY0kdOWeEVi4uiNn8Cs23zwZt1NY040E2w"

echo
echo
echo "Third bad request with a modified token !"
$curl -H "Authorization: Bearer $token" "$target" | tidy -q 2>/dev/null

token=""
echo
echo
echo "Forth bad request with an empty token !"
$curl -H "Authorization: Bearer $token" "$target" | tidy -q 2>/dev/null

#______________________________________________________________________________
