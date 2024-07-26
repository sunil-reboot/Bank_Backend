# Use an OpenJDK image based on Alpine Linux
FROM openjdk:11-jdk

# Install Maven
#RUN apk update && apk add maven
RUN apt-get update && apt-get install -y maven

WORKDIR /code

# Copy Maven configuration
COPY pom.xml /code/

# Download dependencies
RUN mvn dependency:resolve

# Copy source code and build the application
COPY src /code/src
RUN mvn package

# Expose the port the app runs on
EXPOSE 8080

# Define the command to run the application
CMD ["java", "-jar", "target/InvestBetter-0.0.1-SNAPSHOT.jar"]