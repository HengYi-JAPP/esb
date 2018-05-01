# 测试环境运行
# docker run --rm -it -p 9999:9999 --add-host=test.texbeedata.com:120.55.34.81 --mount type=bind,source=/home/esb/esb-open,target=/home/esb/esb-open --name esb-open esb-open

# 推送生产环境
# docker tag esb-open hengyi/esb-open
# docker push hengyi/esb-open

# 生产环境运行
# docker run -d -p 9999:9999 --mount type=bind,source=/home/esb/esb-open,target=/home/esb/esb-open --name esb-open --restart=on-failure:10 hengyi/esb-open


# docker run -d -p 9999:9999 --add-host=test.texbeedata.com:120.55.34.81 --mount type=bind,source=/home/esb/esb-open,target=/home/esb/esb-open --name esb-open --restart=on-failure:10 hengyi/esb-open

# docker exec

FROM java:8-jre-alpine

ENV VERTICLE_HOME /usr/verticles
ENV VERTICLE_FILE esb-open-1.0-all.jar
COPY $VERTICLE_FILE $VERTICLE_HOME/

EXPOSE 9999

WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE"]