# Solicitudes Crediya

Servicio de solicitudes de préstamo implementado con Clean Architecture y Spring WebFlux.

## Tecnologías
- Java 21
- Spring WebFlux (reactivo)
- R2DBC PostgreSQL
- AWS SQS (notificaciones)
- Gradle
- Lombok

## Arquitectura
Sigue Clean Architecture con módulos separados:
- **Domain**: Modelos de negocio (LoanApplication, LoanType, State)
- **UseCase**: Lógica de aplicación
- **Infrastructure**: Adaptadores (R2DBC PostgreSQL) y entry points (reactive-web)
- **Application**: Configuración y punto de entrada

## Instalación y Ejecución

### Prerrequisitos
- Java 21
- PostgreSQL
- Cuenta AWS con permisos para SQS

### Configuración
1. Copia el archivo de ejemplo de variables de entorno:
   ```bash
   cp .env.example .env
   ```

2. Configura las variables de entorno en `.env`:
   ```bash
   # AWS Configuration
   AWS_REGION=us-east-1
   AWS_ACCESS_KEY_ID=your_aws_access_key_here
   AWS_SECRET_ACCESS_KEY=your_aws_secret_access_key_here

   # SQS Configuration
   SQS_QUEUE_URL=https://sqs.us-east-1.amazonaws.com/869935089800/loan-application-notifications
   SQS_ENDPOINT=https://sqs.us-east-1.amazonaws.com
   ```

3. Configura PostgreSQL en el archivo `.env`

### Ejecución
```bash
# Construir el proyecto
./gradlew build

# Ejecutar con perfil de desarrollo
./gradlew bootRun --args='--spring.profiles.active=dev'

# O usando Docker
docker-compose up --build

### Probar Conexión SQS
```bash
# Asegúrate de tener configuradas las variables de entorno
source .env

# Ejecuta el script de prueba
./test-sqs.sh
```
```

## Endpoints

### Solicitudes de Préstamo
- `POST /api/v1/solicitud`: Crear nueva solicitud de préstamo
- `GET /api/v1/solicitud`: Listar solicitudes para revisión (solo Advisor)
- `PUT /api/v1/solicitud/{id}`: Actualizar estado de solicitud (Approved/Rejected)

### Ejemplos de Uso

#### Crear Solicitud
```bash
curl -X POST http://localhost:8081/api/v1/solicitud \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "clientId": "user123",
    "amount": 50000.00,
    "term": 12,
    "loanTypeId": 1
  }'
```

#### Actualizar Estado (Solo Advisor)
```bash
curl -X PUT http://localhost:8081/api/v1/solicitud/550e8400-e29b-41d4-a716-446655440000 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "statusId": 3
  }'
```

## AWS SQS Integration

El servicio envía notificaciones automáticas a una cola SQS cuando se actualiza el estado de una solicitud de préstamo.

### Configuración de Cola SQS
- **URL**: `https://sqs.us-east-1.amazonaws.com/869935089800/loan-application-notifications`
- **Región**: `us-east-1`
- **Mensaje JSON**:
  ```json
  {
    "applicationId": "uuid",
    "clientId": "string",
    "newStatus": 3,
    "decisionTimestamp": "2024-01-15T10:30:00Z",
    "amount": 50000.00,
    "term": 12,
    "loanTypeId": 1
  }
  ```

### Autenticación AWS
El servicio usa el siguiente orden de prioridad para credenciales AWS:
1. Variables de entorno (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`)
2. Propiedades del sistema Java
3. Perfiles AWS (`~/.aws/credentials`)
4. IAM Roles (EC2, ECS, Lambda)

### Permisos IAM Requeridos
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "sqs:SendMessage",
        "sqs:GetQueueUrl"
      ],
      "Resource": "arn:aws:sqs:us-east-1:869935089800:loan-application-notifications"
    }
  ]
}
```
