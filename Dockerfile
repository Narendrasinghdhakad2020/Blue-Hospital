FROM oraclelinux:9

# Update system packages and install latest krb5-libs
RUN yum update -y && yum install -y krb5-libs

# Use an official Java runtime as a parent image
FROM openjdk:23

#set the working directory in the container
WORKDIR /patient-app

#copy application jar file to container
COPY target/spring-patient-app.jar patient-app.jar

# Expose the application port (adjust as per your application)
EXPOSE 8085

#run command to run the container
ENTRYPOINT ["java" ,"-jar","patient-app.jar"]