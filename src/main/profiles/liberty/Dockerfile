FROM open-liberty:microProfile3
MAINTAINER IBM Java engineering at IBM Cloud
COPY --chown=1001:0 ./target/liberty/wlp/usr/servers/defaultServer /config/
COPY --chown=1001:0 ./target/liberty/wlp/usr/extension /opt/ol/wlp/usr/extension/
USER root
RUN apt-get update -y && apt-get install -y curl
USER 1001
