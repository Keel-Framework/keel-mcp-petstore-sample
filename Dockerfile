FROM tomcat-sca:latest
ARG WAR_FILE
COPY ${WAR_FILE} /usr/local/tomcat/webapps/app.war
CMD /usr/local/tomcat/bin/catalina.sh run