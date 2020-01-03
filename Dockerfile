FROM maven:3.6.3-jdk-8 as builder
WORKDIR /app
COPY src .
COPY pom.xml .
RUN mvn clean package

# Based on Azul Zulu
FROM avanadebr/tomcat:9.0.29-zulu-11.35_15-jre-11.0.5_10 as app
RUN  rm -rf ${CATALINA_HOME}/webapps/ROOT
COPY --from=builder /app/target/app.war ./webapps/ROOT.war
