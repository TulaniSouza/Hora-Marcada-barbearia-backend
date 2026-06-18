FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
RUN java -version
RUN mvn -version

COPY pom.xml .

RUN mvn dependency:get -Dartifact=org.projectlombok:lombok:1.18.30 -Ddest=/workspace/lombok.jar

RUN mvn dependency:resolve-plugins dependency:resolve

COPY src src

RUN mvn clean package -DskipTests \
    -Dmaven.compiler.fork=true \
    -Dmaven.compiler.compilerArgs="-J-javaagent:/workspace/lombok.jar" \
    -Dmaven.compiler.annotationProcessorPaths=org.projectlombok:lombok:1.18.30,org.projectlombok:lombok-mapstruct-binding:0.2.0,org.mapstruct:mapstruct-processor:1.5.5.Final

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app

COPY --from=build /workspace/target/Atom_Backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
USER app

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]