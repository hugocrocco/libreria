Librería LiterAlura

Aplicación Java Spring Boot que se conecta a la API pública de Gutendex para buscar, importar y gestionar libros, almacenándolos en una base de datos PostgreSQL.
Permite trabajar tanto con datos en tiempo real desde la API como con datos persistidos en la base local.


Características
	•	Buscar libros en la API por título, idioma o texto libre.
	•	Importar libros desde la API y guardarlos en PostgreSQL.
	•	Listar todos los libros almacenados en la base de datos.
	•	Filtrar libros por idioma desde la base de datos.
	•	Listar autores con sus libros (relación bidireccional en JPA).
	•	Menú interactivo por consola para ejecutar todas las funciones.
	•	API REST para consultas vía endpoints.


Tecnologías utilizadas
	•	Java 17
	•	Spring Boot 3.2.3
	•	Spring Data JPA
	•	PostgreSQL
	•	Maven
	•	Gutendex API (https://gutendex.com/)

 Configuración previa
 Instalar PostgreSQL y crear una base de datos (ej: libreria).
 Configurar credenciales en src/main/resources/application.properties:
 spring.datasource.url=jdbc:postgresql://localhost:5432/libreria
  spring.datasource.username=TU_USUARIO
  spring.datasource.password=TU_PASSWORD
  spring.jpa.hibernate.ddl-auto=update
  spring.jpa.show-sql=true

ejecutar desde la consola   ./mvnw spring-boot:run
