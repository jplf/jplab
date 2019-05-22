# jplf/openfire

# Introduction

The `Dockerfile` is used to create a [Docker](https://www.docker.com/) container image for [Openfire](http://www.igniterealtime.org/projects/openfire/).

The version found here is a violent modification of the package published on [github](https://github.com/sameersbn/docker-openfire).


## Quickstart

Start Openfire using:

```bash
docker run --name openfire -dt --restart=always --publish 9090:9090 --publish 5222:5222 \
  --volume /var/docker/openfire:/var/lib/openfire jplf/openfire
```

* Alternatively, you can use the sample [docker-compose.yml](docker-compose.yml) file to start the container using [Docker Compose](https://docs.docker.com/compose/)*

Point your browser to http://localhost:9090 and follow the setup procedure to complete the installation. The [Build A Free Jabber Server In 10 Minutes](https://www.youtube.com/watch?v=ytUB5qJm5HE#t=246s) video by HAKK5 should help you with the configuration and also introduce you to some of its features.

## Persistence

For the Openfire to preserve its state across container shutdown and startup you should mount a volume at `/var/lib/openfire`.

> *The [Quickstart](#quickstart) command already mounts a volume for persistence.*

## Logs

To access the Openfire logs, located at `/var/log/openfire`, you can use `docker exec`. For example, if you want to tail the logs:

```bash
docker exec -it openfire tail -f /var/log/openfire/info.log
```

# Maintenance

## Upgrading

To upgrade to newer releases:

  2. Stop the currently running image:

  ```
  docker stop openfire
  ```

  3. Remove the stopped container

  ```
  docker rm -v openfire
  ```

  4. Start the updated image

  ```
  docker run -name openfire -d [OPTIONS] jplf/openfire
  ```

## Shell Access

For debugging and maintenance purposes you may want access the containers shell. If you are using Docker version `1.3.0` or higher you can access a running containers shell by starting `bash` using `docker exec`:

```bash
docker exec -it openfire bash
```

# References

  * http://www.igniterealtime.org/projects/openfire/
  * https://github.com/sameersbn/docker-openfire