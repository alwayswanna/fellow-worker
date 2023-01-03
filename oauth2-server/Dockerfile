FROM openjdk:17

ADD target/oauth2-server-*.jar /app.jar
USER appuser

CMD java $JAVA_MEM $JAVA_EXT -jar /app.jar
