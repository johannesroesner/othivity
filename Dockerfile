# setup builder
FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /app

# get dependencies
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# build application
COPY src ./src
RUN ./mvnw package -DskipTests

# run the application in a seperate lightweight container
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar OthivityApplication.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "OthivityApplication.jar"]