#
# HTRC-Portal Dockerfile
#
# https://github.com/htrc/HTRC-Portal
#

# Base image
FROM htrc/java8:alpine

# Install dockerize to handle service dependencies
RUN wget https://github.com/jwilder/dockerize/releases/download/v0.2.0/dockerize-linux-amd64-v0.2.0.tar.gz && \
    tar -C /usr/local/bin -xzf dockerize-linux-amd64-v0.2.0.tar.gz && \
    rm -f dockerize-linux-amd64-v0.2.0.tar.gz

COPY target/universal/stage/ /opt/portal/
WORKDIR /opt/portal/

EXPOSE 9000

CMD dockerize -timeout 120s -wait ${IDP_EP:-tcp://idp:9443} -wait ${MYSQL_EP:-tcp://mysql:3306} ./bin/htrc-portal -Dconfig.file=conf/application.conf -Dlogger.file=conf/logger.xml
