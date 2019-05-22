#_______________________________________________________________________________

# A simple image with chunks of mongoose inside.

# Jean-Paul Le Fèvre - 10 May 2016
#_______________________________________________________________________________

FROM node:5
MAINTAINER Jean-Paul Le Fèvre <lefevre@fonteny.org>

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY package.json /usr/src/app/
RUN npm install

COPY Dockerfile package.json app.js /usr/src/app/
COPY server /usr/src/app/server
COPY public /usr/src/app/public

EXPOSE 8800

ENTRYPOINT ["node", "app.js"]

#_______________________________________________________________________________
