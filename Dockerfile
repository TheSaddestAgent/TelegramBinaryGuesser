FROM maven:3.8-amazoncorretto-21

COPY . .

RUN mvn clean package

CMD ["java", "-jar", "target/BinarySearch-1.0-SNAPSHOT-jar-with-dependencies.jar"]