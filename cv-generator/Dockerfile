FROM openjdk:17
# root access to filesystem.
RUN mkdir "/resume/"

ADD target/cv-generator-*.jar /app.jar
CMD java $JAVA_MEM $JAVA_EXT -jar /app.jar
