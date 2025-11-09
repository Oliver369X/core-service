# Core Service

Microservicio de FinWise responsable de la gestión de cuentas, transacciones, presupuestos y metas de ahorro. Implementado con Spring Boot, GraphQL y MySQL, expone un subgrafo federado consumido por el `gateway-service`.

## Tecnologías clave
- Java 21
- Spring Boot 3.3.x
- Spring GraphQL (Federation v2.5)
- Spring Data JPA (MySQL)
- Testcontainers (MySQL con fallback H2)

## Variables de entorno
| Variable | Descripción | Valor por defecto |
| --- | --- | --- |
| `CORE_SERVICE_PORT` | Puerto de arranque del microservicio | `5010` |
| `CORE_MYSQL_URL` | URL JDBC hacia Mysql | `jdbc:mysql://localhost:3307/finwise_core?...` |
| `CORE_MYSQL_USER` | Usuario de la base | `core_user` |
| `CORE_MYSQL_PASSWORD` | Contraseña de la base | `core_password` |
| `NOTIFICATION_SERVICE_URL` | Endpoint GraphQL del servicio de notificaciones | `http://localhost:5025/graphql` |

## Pruebas
Este servicio utiliza **Testcontainers** para levantar MySQL en los tests de integración. Asegúrate de tener Docker activo.
```bash
mvn test
```

## Ejecución local
### Con MySQL (recomendado)
```bash
mvn spring-boot:run
```

### Sin MySQL (modo rápido con H2)
```bash
mvn spring-boot:run ^
  -Dspring.datasource.url=\"jdbc:h2:mem:finwise_dev;MODE=MySQL\" ^
  -Dspring.datasource.username=sa ^
  -Dspring.datasource.password= ^
  -Dspring.datasource.driver-class-name=org.h2.Driver ^
  -Dspring.jpa.hibernate.ddl-auto=update
```
> Este modo permite validar el esquema GraphQL sin necesidad de levantar el contenedor `core-db`. La aplicación inicializa tablas en memoria y descarta los datos al detenerse.

### Envía notificaciones
El servicio emite una notificación al crear o actualizar presupuestos mediante el microservicio `notification-service`. Ajusta `NOTIFICATION_SERVICE_URL` si cambias la URL en despliegue.

## Docker
```bash
# Construir imagen
docker build -t finwise/core-service .

# Levantar servicio y base de datos MySQL
docker-compose up -d
```

## Esquema GraphQL
El esquema se encuentra en `src/main/resources/graphql/core-schema.graphqls` e incluye entidades:
- `Account`
- `Transaction`
- `Budget`
- `Goal`

## Próximos pasos
- Publicar imagen Docker para despliegue en DigitalOcean.
- Conectar el servicio al `gateway-service` y ampliar resolutores compartidos.
- Incorporar eventos de dominio para disparar notificaciones.
