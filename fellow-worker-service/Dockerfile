FROM openjdk:17

ADD target/fellow-worker-service-*.jar /app.jar
USER appuser

CMD java $JAVA_MEM $JAVA_EXT -jar /app.jar
