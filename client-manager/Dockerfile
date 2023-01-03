FROM openjdk:17

ADD target/client-manager-*.jar /app.jar
USER appuser

CMD java $JAVA_MEM $JAVA_EXT -jar /app.jar