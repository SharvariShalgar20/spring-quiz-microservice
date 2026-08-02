# ---------- Build Stage ----------
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# ---------- Runtime Stage ----------
FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]