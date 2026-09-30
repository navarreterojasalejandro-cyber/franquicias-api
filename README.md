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

La API se conecta a Atlas mediante la variable `MONGODB_URI`. En Atlas, usa un usuario de base de datos con el rol `readWrite` únicamente en la base `franquicias` y agrega la IP de tu equipo en **Network Access**.

En la carpeta del proyecto (junto a `compose.yaml`), crea un archivo `.env` con la cadena del clúster. El archivo debe tener este formato:

```text
MONGODB_URI=mongodb+srv://USUARIO:CONTRASEÑA@TU-CLUSTER.mongodb.net/franquicias?retryWrites=true&w=majority
```

Reemplaza los valores de ejemplo por los de Atlas. Después ejecuta `docker compose up --build`. No compartas ni subas la cadena real al repositorio: `.env` está excluido por `.gitignore`.

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

## Resultado visible en MongoDB Atlas

Se creó la franquicia `Franquicia Demo Entrega` en Atlas y se recorrieron las operaciones de creación, actualización y eliminación. La API respondió `UP`; se confirmaron dos sucursales guardadas y que el producto temporal eliminado ya no aparecía.

La ruta `GET /api/franquicias/724658df-ad18-4dee-bd58-aea8365c0e6d/productos-mayor-stock` devolvió un producto por sucursal:

```json
[
  {
    "branchId": "5c81a848-77e7-4be3-9f70-cd466d39890b",
    "branchName": "Bogota Centro Actualizada",
    "productId": "eb8cfb3e-c45a-46e0-97a5-4d388a464c22",
    "productName": "Cafe Especial",
    "stock": 32
  },
  {
    "branchId": "2b854364-3bb5-4062-b08e-4a5f4e24ae4b",
    "branchName": "Medellin Norte",
    "productId": "d7b24791-4428-4247-8f6d-48ccff5f252c",
    "productName": "Cafe en Grano",
    "stock": 25
  }
]
```

La franquicia de demostración permanece guardada en Atlas para que el resultado se pueda consultar con ese endpoint.
