FROM maven:3.6.3-jdk-8 as builder
WORKDIR /app
COPY src ./src
COPY pom.xml ./
RUN mvn clean install -DskipTests=true

FROM maven:3.6.3-jdk-8
WORKDIR /app
COPY src ./src
COPY pom.xml ./
RUN mvn clean install -DskipTests=true

COPY --from=builder /app/target/mongoinitializer-1.0-SNAPSHOT.jar /app/

RUN mvn clean

CMD [ "java", "-jar", "mongoinitializer-1.0-SNAPSHOT.jar" ]
