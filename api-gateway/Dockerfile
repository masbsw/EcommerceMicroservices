FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

RUN printf '%s\n' \
    '<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">' \
    '  <modelVersion>4.0.0</modelVersion>' \
    '  <groupId>com.ecommerce</groupId>' \
    '  <artifactId>ecommerce-platform</artifactId>' \
    '  <version>0.0.1-SNAPSHOT</version>' \
    '  <packaging>pom</packaging>' \
    '  <properties>' \
    '    <java.version>17</java.version>' \
    '    <spring.boot.version>3.3.12</spring.boot.version>' \
    '    <spring.cloud.version>2023.0.5</spring.cloud.version>' \
    '    <maven.compiler.release>${java.version}</maven.compiler.release>' \
    '  </properties>' \
    '  <dependencyManagement>' \
    '    <dependencies>' \
    '      <dependency>' \
    '        <groupId>org.springframework.boot</groupId>' \
    '        <artifactId>spring-boot-dependencies</artifactId>' \
    '        <version>${spring.boot.version}</version>' \
    '        <type>pom</type>' \
    '        <scope>import</scope>' \
    '      </dependency>' \
    '      <dependency>' \
    '        <groupId>org.springframework.cloud</groupId>' \
    '        <artifactId>spring-cloud-dependencies</artifactId>' \
    '        <version>${spring.cloud.version}</version>' \
    '        <type>pom</type>' \
    '        <scope>import</scope>' \
    '      </dependency>' \
    '    </dependencies>' \
    '  </dependencyManagement>' \
    '  <build>' \
    '    <pluginManagement>' \
    '      <plugins>' \
    '        <plugin>' \
    '          <groupId>org.apache.maven.plugins</groupId>' \
    '          <artifactId>maven-compiler-plugin</artifactId>' \
    '          <version>3.13.0</version>' \
    '          <configuration>' \
    '            <release>${java.version}</release>' \
    '          </configuration>' \
    '        </plugin>' \
    '        <plugin>' \
    '          <groupId>org.springframework.boot</groupId>' \
    '          <artifactId>spring-boot-maven-plugin</artifactId>' \
    '          <version>${spring.boot.version}</version>' \
    '        </plugin>' \
    '      </plugins>' \
    '    </pluginManagement>' \
    '  </build>' \
    '</project>' > /pom.xml

COPY api-gateway/pom.xml pom.xml
COPY api-gateway/src src

RUN mvn -f pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
