FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# 1. Copia as configurações do projeto
COPY pom.xml .

# 2. Força o download do Lombok e de todos os plugins antes do código
RUN mvn dependency:get -Dartifact=org.projectlombok:lombok:1.18.30
RUN mvn dependency:resolve-plugins dependency:resolve

# 3. Copia o código fonte
COPY src src

# 4. Compila injetando o Lombok diretamente como agente do compilador
RUN mvn clean package -DskipTests -Dmaven.compiler.annotationProcessorPaths=org.projectlombok:lombok:1.18.30

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app

COPY --from=build /workspace/target/Atom_Backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

USER app

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]