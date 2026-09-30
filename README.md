# API de franquicias

API REST reactiva para administrar franquicias, sucursales y productos. Está construida con Spring Boot, Spring WebFlux, Spring Data MongoDB reactivo y MongoDB. Cada franquicia se almacena como un documento con sus sucursales y productos embebidos.

## Requisitos

- Docker Desktop con Docker Compose, o Java 17+, Maven 3.9+ y MongoDB.
- Para el entorno en nube: una cuenta/proyecto en MongoDB Atlas, Terraform 1.10+ y credenciales API de Atlas.

## Ejecutar con Docker

Desde esta carpeta:

```bash
docker compose up --build
```

La API queda en `http://localhost:8084`; MongoDB conserva los datos en el volumen `mongo_data`. Para detener los servicios, usa `docker compose down`. Para borrar también la base persistida, usa `docker compose down -v`.

## Ejecutar localmente

Inicia MongoDB en `mongodb://localhost:27017/franquicias` y luego ejecuta:

```bash
mvn spring-boot:run
```

Puedes cambiar la conexión con `MONGODB_URI` y el puerto con `SERVER_PORT`.

## MongoDB Atlas con Terraform

La carpeta `terraform/` aprovisiona un clúster Atlas compartido M0, un usuario de base de datos con acceso de lectura/escritura a `franquicias` y una regla de red restringida al CIDR indicado. Requiere un proyecto Atlas existente. El M0 es adecuado para la prueba y tiene límites propios del nivel compartido.

1. Exporta `MONGODB_ATLAS_CLIENT_ID` y `MONGODB_ATLAS_CLIENT_SECRET` para una clave de servicio de Atlas con permisos sobre el proyecto.
2. Copia `terraform/terraform.tfvars.example` a `terraform/terraform.tfvars`; reemplaza el ID del proyecto, el CIDR público desde el que se conectará la API (`/32` para una IP) y la contraseña.
3. Desde `terraform/`, ejecuta `terraform init`, `terraform plan` y `terraform apply`.
4. Toma el hostname de `terraform output -raw atlas_srv_address` y configura la API con `MONGODB_URI=mongodb+srv://USUARIO:CONTRASEÑA@HOST/franquicias?retryWrites=true&w=majority`.

No subas `terraform.tfvars`, credenciales ni el estado de Terraform al repositorio. El estado puede contener secretos; para un equipo, configura un backend remoto cifrado y acceso restringido. Para conectar una API desplegada, cambia `api_cidr` al rango de salida de ese servicio. Este Terraform aprovisiona la base de datos; la aplicación se puede desplegar como contenedor en el proveedor que elijas usando el `Dockerfile`.

## Endpoints

Todos los recursos están bajo `/api/franquicias`. Los cuerpos y respuestas usan JSON.

| Método | Ruta | Acción |
|---|---|---|
| `POST` | `/api/franquicias` | Crear franquicia: `{"name":"Café Central"}` |
| `GET` | `/api/franquicias` | Listar franquicias |
| `GET` | `/api/franquicias/{franchiseId}` | Obtener una franquicia con sucursales y productos |
| `PATCH` | `/api/franquicias/{franchiseId}` | Cambiar el nombre de la franquicia |
| `POST` | `/api/franquicias/{franchiseId}/sucursales` | Añadir sucursal: `{"name":"Centro"}` |
| `PATCH` | `/api/franquicias/{franchiseId}/sucursales/{branchId}` | Cambiar el nombre de la sucursal |
| `POST` | `/api/franquicias/{franchiseId}/sucursales/{branchId}/productos` | Añadir producto: `{"name":"Latte","stock":12}` |
| `DELETE` | `/api/franquicias/{franchiseId}/sucursales/{branchId}/productos/{productId}` | Eliminar producto |
| `PATCH` | `/api/franquicias/{franchiseId}/sucursales/{branchId}/productos/{productId}/stock` | Cambiar stock: `{"stock":25}` |
| `PATCH` | `/api/franquicias/{franchiseId}/sucursales/{branchId}/productos/{productId}` | Cambiar nombre del producto |
| `GET` | `/api/franquicias/{franchiseId}/productos-mayor-stock` | Producto con más stock por sucursal |

Los IDs se generan automáticamente. La consulta de máximos devuelve una lista con el producto ganador de cada sucursal que tenga productos, junto con el nombre e ID de la sucursal. Si hay empate en una sucursal, devuelve uno de los productos empatados.

Ejemplo de respuesta del endpoint de máximos:

```json
[
  {
    "branchId": "id-sucursal",
    "branchName": "Centro",
    "productId": "id-producto",
    "productName": "Latte",
    "stock": 25
  }
]
```

Los nombres no pueden estar vacíos y el stock debe ser cero o mayor. Los recursos inexistentes responden `404`, los datos inválidos `400` y los cambios concurrentes detectados `409`. La salud del servicio está disponible en `/actuator/health`.

## Diseño

- WebFlux y Project Reactor (`Mono`/`Flux`) para controladores, servicios y repositorio no bloqueantes.
- MongoDB reactivo como persistencia, con franquicia, sucursales y productos en un único documento agregado.
- IDs UUID independientes para franquicias, sucursales y productos.
- Control optimista de concurrencia con `@Version` en el documento de franquicia.
- Compose levanta API y base de datos con comprobación de salud de MongoDB.
