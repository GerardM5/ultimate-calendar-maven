# ==== Build ====
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests clean package

# ==== Runtime ====
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN useradd -ms /bin/bash appuser
USER appuser
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -Dfile.encoding=UTF-8"
ENTRYPOINT ["java","-jar","/app/app.jar"]