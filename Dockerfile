FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn -q package -DskipTests
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/tasktrack.jar tasktrack.jar
COPY --from=build /app/src/main/webapp src/main/webapp
ENV TZ=Asia/Jakarta
EXPOSE 8080
CMD ["java","-jar","tasktrack.jar"]
