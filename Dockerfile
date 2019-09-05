FROM ubuntu:18.04
LABEL traefik.port="8080"

RUN apt-get update && apt-get install -y \
 openjdk-8-jre-headless \
 openjdk-8-jdk-headless \ 
 git \ 
 curl \ 
 build-essential \
 wget \ 
 cargo \
 sqlite3 \
 libsqlite3-dev

ENV MAVEN_VERSION=3.6.2
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

RUN mkdir -p /usr/share/maven \
  && curl -fsSL http://apache.osuosl.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar -xzC /usr/share/maven --strip-components=1 \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

RUN mkdir -p /opt/wire/lib

RUN git clone https://github.com/wireapp/cryptobox4j.git /opt/cryptobox4j && cd /opt/cryptobox4j && make
RUN cp -r /opt/cryptobox4j/build/lib /opt/wire/

RUN git clone https://github.com/m3nix/echo-bot.git /opt/echo-bot && cd /opt/echo-bot/ && mvn package -DskipTests
RUN cp /opt/echo-bot/libs/libblender.so /opt/wire/lib/
RUN cp /opt/echo-bot/target/echo.jar /opt/echo-bot/

EXPOSE 8080 8081

WORKDIR /opt/echo-bot/
CMD ./entrypoint.sh
