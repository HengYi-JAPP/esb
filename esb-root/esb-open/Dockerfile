# 测试环境运行
# docker run --rm -it -p 9999:9999 --add-host=test.texbeedata.com:120.55.34.81 --mount type=bind,source=/home/esb/esb-open,target=/home/esb/esb-open --name esb-open esb-open

# 推送生产环境
# docker tag esb-open hengyi/esb-open
# docker push hengyi/esb-open

# 生产环境运行
# docker run -d -p 9999:9999 --mount type=bind,source=/home/esb/esb-open,target=/home/esb/esb-open --name esb-open --restart=on-failure:10 hengyi/esb-open


# docker run -d -p 9999:9999 --add-host=test.texbeedata.com:120.55.34.81 --mount type=bind,source=/home/esb/esb-open,target=/home/esb/esb-open --name esb-open --restart=on-failure:10 hengyi/esb-open

# docker exec
FROM mcr.microsoft.com/java/jre:11u3-zulu-alpine

ENV TZ Asia/Shanghai
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport"
ENV JAVA_OPTIONS ""

ENV VERTICLE_HOME /usr/verticles
ENV VERTICLE_FILE esb-open-fat.jar
COPY $VERTICLE_FILE $VERTICLE_HOME/

EXPOSE 9996

WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java --add-modules java.se \
--add-exports java.base/jdk.internal.ref=ALL-UNNAMED \
--add-opens java.base/java.lang=ALL-UNNAMED \
--add-opens java.base/java.lang.invoke=ALL-UNNAMED \
--add-opens java.base/java.nio=ALL-UNNAMED \
--add-opens java.base/sun.nio.ch=ALL-UNNAMED \
--add-opens java.management/sun.management=ALL-UNNAMED \
--add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED \
$JAVA_OPTIONS \
-jar $VERTICLE_FILE \
-conf /home/esb/esb-open/config.json \
-Dvertx.metrics.options.enabled=true \
-Dvertx.metrics.options.jmxEnabled=true"]
