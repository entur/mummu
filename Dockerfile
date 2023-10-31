FROM eclipse-temurin:21.0.1_12-jdk-alpine

RUN apk update && apk upgrade && apk add --no-cache \
    tini

WORKDIR /deployments
COPY target/mummu-*-SNAPSHOT.jar /deployments/mummu.jar

COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod a+x /docker-entrypoint.sh
ENTRYPOINT ["sh", "/docker-entrypoint.sh"]

RUN addgroup appuser && adduser --disabled-password appuser --ingroup appuser
RUN chown -R appuser:appuser /deployments
USER appuser

CMD [ "/sbin/tini", "--", "java", "-jar", "/deployments/mummu.jar" ]