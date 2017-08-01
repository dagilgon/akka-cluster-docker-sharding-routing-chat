FROM java:latest
MAINTAINER David Gilsanz Gonzalez
WORKDIR /opt/docker
ADD opt /opt
RUN ["chown", "-R", "daemon:daemon", "."]
USER daemon
ENTRYPOINT ["bin/akka-docker"]
CMD []
