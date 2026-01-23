FROM eclipse-temurin:21-jdk AS build

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

RUN ./mvnw -B dependency:go-offline                          

COPY src src

RUN ./mvnw -B package                                        

FROM eclipse-temurin:21-jre

COPY --from=build target/spring-boot-admin-0.0.2-SNAPSHOT.jar .

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "spring-boot-admin-0.0.2-SNAPSHOT.jar"]
