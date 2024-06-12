# Leer primero
El proyecto fue creado para aprendizaje. Se desarrollo con el fin de aprender y aplicar conocimientos del lenguaje de programación Java, Spring Boot, Angular, Pruebas Unitarias, Junit, Mockito, Docker, SOLID, API REST. Los datos que se encuentran en la base de datos son ficticios y no representan a ninguna persona o entidad.

# Sistema-pago
Este proyecto es un sistema de procesamiento de pagos desarrollado en Java utilizando el Spring Framework. Incluye funcionalidades para realizar transacciones con tarjetas, con validaciones específicas y reglas de negocio implementadas. Además, se ha diseñado para seguir principios de diseño SOLID, garantizando un código limpio y mantenible.
La documentación la puedes encontrar en [documentación](https://drive.google.com/drive/folders/1vUBCkzgZM8efa4chF9swzLUzf2uSlbvw?usp=drive_link)

# Requisitos
- Java 17 o superior
- Maven 3.6 o superior
- Docker (opcional, para ejecutar la aplicación en un contenedor)
- Base de datos: H2 en memoria (una vez se deje de ejecutar el programa los datos se borran)

# Instalación con Docker
Clona el repositorio:
```sh
git clone https://github.com/Axel-Guzman-20/Sistema-pago-.git
```
Entrar en la carpeta
```sh
cd sistema-de-pagos
```
Construye la imagen de Docker:
```sh
docker build -t sistema-pago-backend .
```
Ejecuta el contenedor:
```sh
docker run -p 8080:8080 sistema-pago-backend
```
Los endpoints se podran probar con POSTMAN en http://localhost:8080/EndPointaUtilizar o  bien, utilizando la interfaz creada para el proyecto:[interfaz del proyecto](https://github.com/Axel-Guzman-20/sistema-pago-interfaz)


Para visualizar La base de datos H2 puedes ir a http://localhost:8080/h2-console y llenar los siguientes campos para acceder:
- JDBC URL: jdbc:h2:mem:testdb
- User Name: sa
- Password: password


# Instalación sin uso de Docker
Clona el repositorio:
```sh
git clone https://github.com/tuusuario/sistema-de-pagos.git
```
Entrar en la carpeta
```sh
cd sistema-de-pagos
```
Compila el proyecto con Maven:
```sh
mvn clean install
```
Para ejecutar la aplicación localmente, usa el siguiente comando de Maven o bien, ejecutalo desde el IDE que estes utilizando:
```sh
mvn spring-boot:run
```
Los endpoints se podran probar con POSTMAN en http://localhost:8080/EndPointaUtilizar o bien, utilizando la interfaz creada para el proyecto: [interfaz del proyecto](https://github.com/Axel-Guzman-20/sistema-pago-interfaz)

Para visualizar La base de datos H2 puedes ir a http://localhost:8080/h2-console y llenar los siguientes campos para acceder:
- JDBC URL: jdbc:h2:mem:testdb
- User Name: sa
- Password: password
