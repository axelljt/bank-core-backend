# Usamos una imagen ligera de Java 21 (o la versión que definiste en el pom)
FROM eclipse-temurin:21-jdk-alpine

# Definimos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el JAR generado a la imagen
COPY target/banking-api.jar app.jar

# Exponemos el puerto que usa Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]