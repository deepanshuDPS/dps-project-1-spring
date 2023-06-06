FROM tomcat:jdk17-temurin
COPY target/v1.war /usr/local/tomcat/webapps/
EXPOSE 8080
CMD ["catalina.sh", "run"]


