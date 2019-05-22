#_______________________________________________________________________________

# A simplified Apache httpd server with proxy modules enabled

# Jean-Paul Le Fèvre -  29 october 2018

# Usage : docker build -t jplf/http-proxy:0.1 .

# Check it :
# docker run --rm -d --name=myproxy -p 8080:80 \
#            -v "$PWD"/htdocs:/usr/local/apache2/htdocs \
#            -v "$PWD"/conf:/usr/local/apache2/conf \
#            jplf/http-proxy
#_______________________________________________________________________________

FROM httpd:alpine
MAINTAINER Jean-Paul Le Fèvre <lefevre@fonteny.org>

ENV USR nobody
ENV GRP nogroup

# The container will be started by this user
# USER $USR:$GRP

EXPOSE 80
CMD ["httpd-foreground"]
#_______________________________________________________________________________
