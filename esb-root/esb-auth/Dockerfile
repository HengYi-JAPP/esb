FROM mcr.microsoft.com/java/jre:11u3-zulu-alpine

ENV TZ Asia/Shanghai
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport"
ENV JAVA_OPTIONS ""

ENV VERTICLE_HOME /usr/verticles
ENV VERTICLE_FILE esb-auth-fat.jar
COPY $VERTICLE_FILE $VERTICLE_HOME/

EXPOSE 9998

WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.invoke=ALL-UNNAMED $JAVA_OPTIONS -jar $VERTICLE_FILE -conf /home/esb/esb-auth/config.json -Dvertx.metrics.options.enabled=true -Dvertx.metrics.options.jmxEnabled=true"]