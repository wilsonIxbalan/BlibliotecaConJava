# Sistema de Biblioteca en Java

Este proyecto es un sistema de Biblioteca desarrollado en Java, utilizando el patrón de arquitectura MVC. Permite la gestión de libros y usuarios a través de una interfaz gráfica construida con **Swing Designer**. El proyecto utiliza **Maven** para la gestión de dependencias y realiza la conexión a una base de datos SQL Server configurada en el entorno local.

## Características principales

- Gestión de libros y usuarios.
- Interfaz gráfica amigable desarrollada con Swing.
- Arquitectura basada en el patrón MVC.
- Uso de Maven para la gestión de dependencias.

## Tecnologías utilizadas

- **Java**
- **Swing Designer** (Interfaz gráfica)
- **Maven**
- **SQL Server** (Base de datos local)

## Requisitos previos

- [Java JDK 8+](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
- [Maven](https://maven.apache.org/)
- SQL Server instalado y configurado en el entorno local

## Instalación

1. **Clona el repositorio:**
   ```bash
   git clone https://github.com/wilsonIxbalan/BlibliotecaConJava.git
   cd BlibliotecaConJava
   ```

2. **Configura la conexión a la base de datos**  
   Edita el archivo de configuración correspondiente para que los datos de conexión coincidan con tu instancia local de SQL Server.

3. **Compila y ejecuta el proyecto**
   ```bash
   mvn clean install
   mvn exec:java
   ```

## Estructura del proyecto

- `src/` — Código fuente organizado según el patrón MVC.
- `resources/` — Archivos de configuración y recursos.
- `pom.xml` — Archivo de dependencias Maven.

## Contribuciones

¡Las contribuciones son bienvenidas! Si deseas mejorar este proyecto, por favor abre un issue o realiza un pull request.

## Licencia

Este proyecto está licenciado bajo la MIT License.

---

Desarrollado por [wilsonIxbalan](https://github.com/wilsonIxbalan)