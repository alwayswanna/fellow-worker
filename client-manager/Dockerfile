FROM openjdk:17
RUN useradd appuser

ADD target/client-manager-*.jar /app.jar

USER appuser
CMD java $JAVA_MEM $JAVA_EXT -jar /app.jar