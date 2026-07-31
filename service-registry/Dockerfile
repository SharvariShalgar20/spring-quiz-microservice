FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

# Now copy the actual source code
COPY src ./src

# Build the jar, skip tests (tests should run in CI, not during image build)
RUN ./mvnw clean package -DskipTests -B

# ---------- Runtime Stage ----------
FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

# Copy only the built jar from the build stage — nothing else
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]