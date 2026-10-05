# ---------- Stage 1: build the WAR with Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Tests already ran in the Jenkins "Test" stage, so skip them here
RUN mvn clean package -DskipTests

# ---------- Stage 2: run the app on Tomcat ----------
FROM tomcat:10.1-jdk17
# Deploy the WAR as the ROOT application
COPY --from=build /app/target/devops-webapp.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
