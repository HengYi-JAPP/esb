FROM java:8-jre-alpine

ENV VERTICLE_HOME /usr/verticles
ENV VERTICLE_FILE esb-weixin-fat.jar
COPY $VERTICLE_FILE $VERTICLE_HOME/

EXPOSE 9995

WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE -conf /home/esb/esb-weixin/config.json"]