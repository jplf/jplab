#_______________________________________________________________________________

# A simplified linux environment based on slackware 14.1

# Jean-Paul Le Fèvre - 15 April 2016 - 15 June 2018

# Usage : docker build -t jplf/jpware:base .

# Check it :
# docker run --rm -it --name=myslack jplf/jpware:base bash
#_______________________________________________________________________________

FROM zakame/slackware
MAINTAINER Jean-Paul Le Fèvre <lefevre@fonteny.org>

ENV USR jplf
ENV GRP users

RUN removepkg bsd-games cpio diffutils findutils gawk mpfr\
              gnupg groff ncurses patch man infozip sharutils
COPY skel /root

# The container will be started by this user
USER $USR:$GRP

#_______________________________________________________________________________
