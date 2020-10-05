from nexus.corp.bankbtpn.co.id:50001/cakra2/cakra-base-java8:latest

ADD target/ms-websocket.jar /opt/ms-websocket.jar
ENV TZ=Asia/Jakarta

expose 8080

#please define java_opts in your environment example JAVA_OPTS=-Xmx512m

cmd java $JAVA_OPTS -jar /opt/ms-websocket.jar