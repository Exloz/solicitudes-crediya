# Solicitudes Crediya

Servicio de solicitudes de préstamo implementado con Clean Architecture y Spring WebFlux.

## Tecnologías
- Java 21
- Spring WebFlux (reactivo)
- R2DBC PostgreSQL
- Gradle
- Lombok

## Arquitectura
Sigue Clean Architecture con módulos separados:
- **Domain**: Modelos de negocio (LoanApplication, LoanType, State)
- **UseCase**: Lógica de aplicación
- **Infrastructure**: Adaptadores (R2DBC PostgreSQL) y entry points (reactive-web)
- **Application**: Configuración y punto de entrada

## Instalación y Ejecución
1. Clona el repositorio
2. Configura PostgreSQL y actualiza `application.yaml`
3. Ejecuta: `./gradlew build`
4. Inicia: `./gradlew bootRun`

## Endpoints
- `POST /api/v1/solicitud`: Crear solicitud de préstamo con validaciones
