FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD build/libs/kotlin-rx-test*.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
EXPOSE 10000