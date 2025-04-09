# Turnos App

Aplicación web para la gestión de turnos en un negocio de estética (manicura, uñas, etc.). Las clientas pueden registrarse e iniciar sesión, visualizar los turnos disponibles y reservarlos. El administrador puede gestionar profesionales y disponibilidades, y cada profesional puede visualizar sus reservas.

## ✨ Características

- Registro e inicio de sesión con JWT.
- Seguridad con Spring Security y roles (`ADMIN`, `PROFESIONAL`, `CLIENTE`).
- Gestión de usuarios, profesionales y turnos.
- Integración con base de datos PostgreSQL.
- Migraciones con Flyway.
- Tests unitarios e integración con H2.
- API REST lista para ser consumida.

## 🛠️ Tecnologías

- **Java 17**
- **Spring Boot 3.4.4**
- **Spring Security**
- **JWT (JSON Web Token)**
- **JPA / Hibernate**
- **Flyway**
- **PostgreSQL (producción)** / **H2 (tests)**
- **Gradle**
- **JUnit + Mockito**

## 🚀 Levantar el proyecto

### 1. Clonar el repositorio:

   ```bash
   git clone https://github.com/Ger1211/turnos.git
   cd turnos
   ```

### 2. Configurar `application.properties`:

  ```properties
  # PostgreSQL
  spring.datasource.url=jdbc:postgresql://localhost:5432/turnos
  spring.datasource.username=tu_usuario
  spring.datasource.password=tu_contraseña
  
  # Flyway
  spring.flyway.enabled=true
  
  # JWT
  jwt.secret=claveSecretaSuperSegura
  ```

### 3. Ejecutar las migraciones y levantar el proyecto:

  ```bash
  ./gradlew flywayMigrate bootRun
  ```

### 4. Para ejecutar tests

  ```bash
  ./gradlew test
  ```

### 5.Estructura del proyecto

  ```swift
  src/
├── main/
│   ├── java/com/german/cabrera/turnos/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── model/
│   │   ├── repository/
│   │   └── security/
│   │   └── service/
│   └── resources/
│       ├── application.properties
│       └── db/migration/         <- Migraciones Flyway
├── test/
│   └── java/com/german/cabrera/turnos/
│       ├── unit/
│       └── integration/
│   └── resources/
│       ├── application-test.properties     <- Config H2

  ```

🧪 Tests

Incluye tests unitarios y de integración. Usa H2 como base de datos en memoria durante los tests para mayor velocidad y aislamiento.

📜 Licencia

Este proyecto está bajo la licencia MIT.
Ver el archivo [LICENSE](./LICENSE) para más detalles.

Proyecto en desarrollo 🚧
Autor: [Ger1211](https://github.com/Ger1211)
