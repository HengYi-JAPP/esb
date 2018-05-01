FROM java:8-jre-alpine

ENV VERTICLE_HOME /usr/verticles
ENV VERTICLE_FILE esb-auth-fat.jar
COPY $VERTICLE_FILE $VERTICLE_HOME/

EXPOSE 9998

WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE -conf /home/esb/esb-auth/config.json -Dvertx.metrics.options.enabled=true -Dvertx.metrics.options.jmxEnabled=true"]