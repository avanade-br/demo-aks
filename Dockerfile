# Based on Azul Zulu
FROM avanadebr/tomcat:9.0.30-zulu-11.35_15-jre-11.0.5_10 as app
RUN  rm -rf ${CATALINA_HOME}/webapps/ROOT
COPY target/app.war ${CATALINA_HOME}/webapps/ROOT.war