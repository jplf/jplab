Here is a list of useful tips for working with MariaDB

**From the newer to the older**

**In a stack**

Prepare a stack to start the db
```
docker secret create mariadb_password
docker network create sonarnet
```

```
docker stack deploy -c docker-compose.yml sonardb
docker service ls
docker run -it --rm mariadb:10.4 mysql -h svom-fsc-0 -uroot -p
```

```
use mysql;
select host, user, password from user;
create database sonar;
create user lefevre@'%' identified by 'mot2passe';
create user lefevre@'localhost' identified by 'mot2passe';
grant all privileges on sonar.* to 'lefevre'@'%';

```

Open ports @ Orsay : 20000-25000

**A new instance**

A new instance of the server can be installed from the [docker hub](https://hub.docker.com/_/mariadb)

Start the server :

`docker run --network host --name mydb -e MYSQL_ROOT_PASSWORD=mot2passe -d mariadb`

Connect to the server :

`docker run -it --network host --rm mariadb mysql -h 127.0.0.1 -uroot -p`

Create a user :

```
create user lefevre@'%' identified by 'Jaypee';
grant all privileges on jpdb.* to lefevre@'%';
```