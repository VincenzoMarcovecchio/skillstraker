# ==============================================================================
# SkillTrack - Java Servlet & JSP Web App Dockerfile for Render
# ==============================================================================

# Stage 1: Build the WAR with Maven
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy POM and sources
COPY pom.xml .
COPY src ./src

# Package WAR
RUN mvn clean package -DskipTests

# Stage 2: Run with Apache Tomcat 10.1 (Jakarta EE 10 / Servlet 6.0)
FROM tomcat:10.1-jdk17-temurin-jammy
LABEL maintainer="SkillTrack"

# Remove default Tomcat apps to serve ROOT directly
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy packaged WAR to ROOT.war
COPY --from=builder /app/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war

# Expose Render default port
EXPOSE 8080

# Configure Tomcat to dynamically bind to Render's $PORT environment variable
CMD ["/bin/bash", "-c", "sed -i \"s/port=\\\"8080\\\"/port=\\\"${PORT:-8080}\\\"/g\" /usr/local/tomcat/conf/server.xml && catalina.sh run"]
