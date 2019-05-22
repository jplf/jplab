# Get used to working with Postgresql

[Tutorial] (http://www.postgresql.org/docs/9.3/static/index.html)

## Dockerization
```
# See also mariadb
docker secret create postgres_password

docker stack deploy -c docker-compose.yml sonardb
docker service ls
docker run -it --rm postgres:11.2 psql -h localhost -U postgres
```

```
select version();
create database sonar;
create user sonar with encrypted password 'mot2passe';
grant all privileges on database sonar to sonar;
\c sonar
\q

```

## Getting started

```
/etc/init.d/postgresql status
/etc/init.d/postgresql start 9.3

su - postgresql
createuser -d -E -P a_login
psql -c '\du'</code> # List users
createdb network
psql -h svomtest.svom.fr network

```


