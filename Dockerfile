# --- STEP 1: Build Stage ---
# Use Maven to compile the Java code
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# --- STEP 2: Run Stage ---
# Use a lightweight Java Runtime for the actual server
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the compiled .jar file from the Build Stage
COPY --from=build /app/target/file-converter-0.0.1-SNAPSHOT.jar app.jar

# Tell Render we are using Port 8080 (standard for Spring Boot)
EXPOSE 8080

# The "Magic" Command:
# 1. Sets memory limits so the Free Tier doesn't crash (-Xmx)
# 2. Maps the server port to Render's dynamic PORT variable
ENTRYPOINT ["java", "-Xmx384m", "-Xms256m", "-jar", "app.jar", "--server.port=${PORT:8080}"]