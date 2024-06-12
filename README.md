
El proyecto fue creado para aprendizaje. Se desarrollo con el fin de aprender y aplicar conocimientos del lenguaje de programación Java, Spring Boot, Angular, Pruebas Unitarias, Junit, Mockito, Docker, SOLID, API REST. Los datos que se encuentran en la base de datos son ficticios y no representan a ninguna persona o entidad.

# Sistema-pago
Este proyecto es un sistema de procesamiento de pagos desarrollado en Java utilizando el Spring Framework. Incluye funcionalidades para realizar transacciones con tarjetas, con validaciones específicas y reglas de negocio implementadas. Además, se ha diseñado para seguir principios de diseño SOLID, garantizando un código limpio y mantenible.

# Requisitos
- Java 17 o superior
- Maven 3.6 o superior
- Docker (opcional, para ejecutar la aplicación en un contenedor)
- Base de datos: H2 en memoria (una vez se deje de ejecutar el programa los datos se borran)

# Instalación con Docker
Clona el repositorio:
`git clone https://github.com/tuusuario/sistema-de-pagos.git`

Entrar en la carpeta
`cd sistema-de-pagos`

Construye la imagen de Docker:
`docker build -t sistema-pago-backend .`

Ejecuta el contenedor:
`docker run -p 8080:8080 sistema-pago-backend`

Los endpoints estarán disponibles en http://localhost:8080

Para visualizar La base de datos H2 puedes ir a http://localhost:8080/h2-console y llenar los siguientes campos para acceder:
- JDBC URL: jdbc:h2:mem:testdb
- User Name: sa
- Password: password


# Instalación sin uso de Docker
Clona el repositorio:
`git clone https://github.com/tuusuario/sistema-de-pagos.git`

Entrar en la carpeta
`cd sistema-de-pagos`

Compila el proyecto con Maven:
`mvn clean install`

Para ejecutar la aplicación localmente, usa el siguiente comando de Maven o bien ejecutalo desde el IDE que estes utilizando:
`mvn spring-boot:run`

Los endpoints estarán disponibles en http://localhost:8080

Para visualizar La base de datos H2 puedes ir a http://localhost:8080/h2-console y llenar los siguientes campos para acceder:
- JDBC URL: jdbc:h2:mem:testdb
- User Name: sa
- Password: password

- 
