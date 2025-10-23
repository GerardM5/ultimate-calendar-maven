# ==== Build ====
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
# Cache de dependencias
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
# Build del proyecto
COPY src ./src
RUN mvn -q -DskipTests clean package

# ==== Runtime (JRE) ====
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Crea user no root
RUN useradd -ms /bin/bash appuser
USER appuser
# Copia jar (ajusta nombre si usas spring-boot:repackage genera *-SNAPSHOT.jar)
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

# Puerto típico
EXPOSE 8080
# Flags JVM ligeritos
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseZGC -Dfile.encoding=UTF-8"
# Perfil de Spring (ajústalo en Dokploy si quieres)
ENV SPRING_PROFILES_ACTIVE=prod

# Healthcheck (si tienes Actuator)
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
 CMD wget -qO- http://localhost:8080/actuator/health | grep '"status":"UP"' || exit 1

ENTRYPOINT ["java","-jar","/app/app.jar"]