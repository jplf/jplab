#_______________________________________________________________________________

# My first attempt to create an image.

# Jean-Paul Le Fèvre - 15 April 2016
#_______________________________________________________________________________

FROM node:5
MAINTAINER Jean-Paul Le Fèvre <lefevre@fonteny.org>

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY package.json /usr/src/app/
RUN npm install
COPY . /usr/src/app/
EXPOSE 8000
CMD [ "npm", "start" ]

#_______________________________________________________________________________
