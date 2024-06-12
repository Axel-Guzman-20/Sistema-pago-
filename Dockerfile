# Usa una imagen de Maven para construir la aplicación Spring Boot
FROM maven:3.8.1-openjdk-17 AS build

# Establece el directorio de trabajo en el contenedor
WORKDIR /app

# Copia el archivo pom.xml y descarga las dependencias
COPY pom.xml .

# Descarga las dependencias del proyecto
RUN mvn dependency:go-offline -B

# Copia el resto de los archivos del proyecto al contenedor
COPY . .

# Compila la aplicación Spring Boot
RUN mvn clean package -DskipTests

# Usa una imagen de OpenJDK para ejecutar la aplicación Spring Boot
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo en el contenedor
WORKDIR /app

# Copia el archivo JAR compilado desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto en el que se ejecutará la aplicación Spring Boot
EXPOSE 8080

# Comando predeterminado para ejecutar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]