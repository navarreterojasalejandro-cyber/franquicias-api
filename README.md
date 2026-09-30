# API de franquicias

API sencilla para administrar franquicias, sucursales, productos y existencias. Está hecha con Spring Boot WebFlux y MongoDB reactivo.

## Iniciar en local

Necesitas Docker Desktop.

1. Abre una terminal en la carpeta del proyecto.
2. Ejecuta:

   ```bash
   docker compose up --build
   ```

Esto inicia la API en `http://localhost:8084` y una base MongoDB local. Para comprobar que está activa, abre `http://localhost:8084/actuator/health`; debe responder `{"status":"UP"}`.

Detén la aplicación con `Ctrl+C` y luego ejecuta `docker compose down`. Los datos de MongoDB quedan guardados en el volumen de Docker.

### Usar MongoDB Atlas

La API también puede conectarse a Atlas. En Atlas, crea un usuario de base de datos con el rol `readWrite` únicamente en la base `franquicias`, agrega la IP de tu equipo en **Network Access** y copia la cadena de conexión del clúster.

En PowerShell, configura la cadena como variable de entorno antes de iniciar Docker:

```powershell
$env:MONGODB_URI = "mongodb+srv://USUARIO:CONTRASEÑA@TU-CLUSTER.mongodb.net/franquicias?retryWrites=true&w=majority"
docker compose up --build
```

Reemplaza los valores de ejemplo por los de Atlas. No compartas ni subas la cadena real al repositorio. El archivo `.gitignore` excluye los archivos `.env` para evitar publicar credenciales.

## Operaciones disponibles

Todas las rutas empiezan con `/api/franquicias`. Los identificadores se generan automáticamente y se obtienen de la respuesta de cada operación.

| Método | Ruta | Resultado |
|---|---|---|
| `POST` | `/api/franquicias` | Crea una franquicia. Recibe `{"name":"Café Central"}` y devuelve `201` con el ID generado. |
| `GET` | `/api/franquicias` | Lista las franquicias. |
| `GET` | `/api/franquicias/{franchiseId}` | Consulta una franquicia y sus sucursales. |
| `PATCH` | `/api/franquicias/{franchiseId}` | Cambia el nombre de la franquicia. Recibe `{"name":"Nuevo nombre"}`. |
| `POST` | `/api/franquicias/{franchiseId}/sucursales` | Agrega una sucursal. Recibe `{"name":"Centro"}` y devuelve `201`. |
| `PATCH` | `/api/franquicias/{franchiseId}/sucursales/{branchId}` | Cambia el nombre de una sucursal. |
| `POST` | `/api/franquicias/{franchiseId}/sucursales/{branchId}/productos` | Agrega un producto. Recibe `{"name":"Café","stock":15}` y devuelve `201`. |
| `PATCH` | `/api/franquicias/{franchiseId}/sucursales/{branchId}/productos/{productId}` | Cambia el nombre de un producto. |
| `PATCH` | `/api/franquicias/{franchiseId}/sucursales/{branchId}/productos/{productId}/stock` | Actualiza el stock. Recibe `{"stock":25}`. |
| `DELETE` | `/api/franquicias/{franchiseId}/sucursales/{branchId}/productos/{productId}` | Elimina un producto y responde `204`. |
| `GET` | `/api/franquicias/{franchiseId}/productos-mayor-stock` | Devuelve el producto con más stock de cada sucursal, indicando sucursal, producto y stock. |

Los nombres son obligatorios y no pueden quedar vacíos. El stock debe ser cero o mayor. Los recursos inexistentes responden `404`; los datos inválidos, `400`.

## Ejemplo de consulta de mayor stock

```http
GET /api/franquicias/{franchiseId}/productos-mayor-stock
```

Respuesta de ejemplo:

```json
[
  {
    "branchId": "id-sucursal-1",
    "branchName": "Bogota Centro",
    "productId": "id-producto-1",
    "productName": "Capuccino Especial",
    "stock": 30
  },
  {
    "branchId": "id-sucursal-2",
    "branchName": "Medellin Norte",
    "productId": "id-producto-2",
    "productName": "Cafe",
    "stock": 15
  }
]
```

La consulta omite sucursales que todavía no tienen productos. Si hay empate en una sucursal, devuelve uno de los productos con el stock máximo.

## Requisitos cubiertos

- Spring Boot con API reactiva usando WebFlux y Spring Data MongoDB Reactive.
- Persistencia en MongoDB: local con Docker Compose o en MongoDB Atlas.
- Endpoints para crear franquicias, sucursales y productos; borrar productos y actualizar stock.
- Consulta del producto con mayor stock por sucursal.
- Endpoints adicionales para cambiar nombres de franquicias, sucursales y productos.
- Dockerfile y Docker Compose para ejecutar la solución localmente.
- Carpeta `terraform/` con configuración de ejemplo para aprovisionar recursos en MongoDB Atlas.

## Resultado de una prueba

En la API local se consultó una franquicia con dos sucursales y se verificó el chequeo de salud:

```json
{"groups":["liveness","readiness"],"status":"UP"}
```

La ruta `GET /api/franquicias/{franchiseId}/productos-mayor-stock` devolvió el producto con más stock de cada sucursal:

```json
[
  {
    "branchId": "58bff7f9-53e3-4a47-a26c-579a81028c3d",
    "branchName": "Bogota Centro Actualizada",
    "productId": "ada3b52c-4b6d-44e4-8d9a-51e510dfbad8",
    "productName": "Capuccino Especial",
    "stock": 30
  },
  {
    "branchId": "73d5fc3a-b369-4aba-ae6e-ace05c347001",
    "branchName": "Medellin Norte",
    "productId": "ee7e8c4b-bca2-469e-8073-9d83a4f73dd9",
    "productName": "Cafe",
    "stock": 15
  }
]
```

La franquicia ya contiene los nombres actualizados. En la prueba del flujo también se verificó que el producto eliminado dejó de aparecer.
