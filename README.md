# LiterAlura

📚 **LiterAlura** es una aplicación de consola desarrollada en Java con Spring Boot que permite buscar, listar y explorar libros y autores usando datos consumidos de APIs externas, con persistencia local en base de datos.

---

## Descripción

LiterAlura facilita la exploración literaria desde la consola con un menú interactivo que ofrece funcionalidades como:

- Buscar libros por título o por autor.  
- Listar autores y libros registrados en la base de datos.  
- Consultar libros cuyos autores estuvieron vivos en un rango de años.  
- Filtrar libros por idioma.  
- Mostrar el top 10 de libros más descargados.  

El proyecto utiliza Spring Boot para la gestión de dependencias y la arquitectura en capas, implementando servicios, repositorios, DTOs y mapeadores.

---

## Tecnologías

- Java 23  
- Spring Boot  
- JPA / Hibernate  
- Base de datos MySQL  
- Maven  

---

## Requisitos previos

- JDK 23 o superior instalado  
- Maven instalado  
- Base de datos configurada y accesible (MySQL)  
- Acceso a internet para consumir las APIs externas  

---

## Instalación

1. Clona el repositorio:

```bash
git clone https://github.com/Gabeust/LiterAlura-ONE.git
cd LiterAlura-ONE
Configura tu base de datos en application.properties o application.yml.

Ejecuta el proyecto con Maven:

mvn spring-boot:run
```

## Uso

Al iniciar, verás un menú interactivo en la consola con las siguientes opciones:

Buscar libro por título

Buscar libros por autor

Listar autores registrados

Listar libros registrados

Libros con autores vivos en un rango de años

Listar libros por idioma

Top 10 libros más descargados

Salir

Sigue las instrucciones en pantalla para ingresar datos y navegar entre las opciones.

---

## Estructura del proyecto

model/ : Entidades JPA para autor y libro.

dto/ : Registros (records) para transferencia de datos entre capas.

repository/ : Interfaces para acceso a base de datos mediante JPA.

service/ : Servicios que contienen la lógica de negocio y consumo de APIs externas.

mapper/ : Clases para mapear entre entidades y DTOs.

Main.java : Componente principal que contiene el menú interactivo y orquesta las llamadas a los servicios.

---
## Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo LICENSE para más detalles.
