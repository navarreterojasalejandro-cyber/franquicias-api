# API de franquicias

Este proyecto sirve para crear franquicias, agregarles sucursales y administrar los productos y el stock de cada sucursal. Está hecho con Spring Boot y guarda los datos en MongoDB.

## Cómo iniciarlo

1. Abre Docker Desktop.
2. En una terminal, entra a la carpeta del proyecto y ejecuta:

   ```bash
   docker compose up --build
   ```

3. Cuando termine, la API estará disponible en `http://localhost:8084`.
4. Para comprobar que está activa, abre `http://localhost:8084/actuator/health`. Debe aparecer `"status":"UP"`.

Para detenerla, presiona `Ctrl+C` y ejecuta `docker compose down`. Los datos quedan guardados en Docker.

## Qué permite hacer

- Crear y consultar franquicias.
- Agregar sucursales a una franquicia.
- Agregar productos, cambiarles el nombre, actualizar su stock y eliminarlos.
- Consultar cuál es el producto con más stock de cada sucursal de una franquicia.
- Cambiar el nombre de una franquicia o una sucursal.

## Rutas principales

Todas empiezan con `/api/franquicias`.

| Método | Ruta | Para qué sirve |
|---|---|---|
| `POST` | `/` | Crear una franquicia (`{"name":"Café Central"}`) |
| `POST` | `/{id}/sucursales` | Agregar una sucursal (`{"name":"Centro"}`) |
| `POST` | `/{id}/sucursales/{sucursalId}/productos` | Agregar producto (`{"name":"Latte","stock":12}`) |
| `DELETE` | `/{id}/sucursales/{sucursalId}/productos/{productoId}` | Eliminar un producto |
| `PATCH` | `/{id}/sucursales/{sucursalId}/productos/{productoId}/stock` | Cambiar stock (`{"stock":25}`) |
| `GET` | `/{id}/productos-mayor-stock` | Ver el producto con más stock de cada sucursal |

También hay rutas `GET` para consultar franquicias y rutas `PATCH` para cambiar nombres. Los identificadores se generan automáticamente. El stock no puede ser negativo y los nombres no pueden quedar vacíos.

## Resultado de una prueba

Se probó crear una franquicia con dos sucursales, agregar productos, cambiar nombres y stock, eliminar un producto y consultar los productos con más stock. La API respondió correctamente (`UP`) y la consulta final mostró:

```text
Bogota Centro Actualizada: Capuccino Especial (stock 30)
Medellin Norte: Cafe (stock 15)
```

El producto eliminado ya no apareció en la consulta.

## Base de datos en la nube

El proyecto incluye `render.yaml` para publicar la API en Render con Docker. Para completar la publicación:

1. En [MongoDB Atlas](https://www.mongodb.com/atlas), crea un proyecto y un clúster gratuito M0. Crea un usuario de base de datos y guarda su contraseña.
2. En Atlas, en **Network Access**, agrega `0.0.0.0/0` para que Render pueda conectarse. Es una configuración sencilla para esta prueba; para producción se debe restringir el acceso.
3. En [Render](https://render.com), crea un **Blueprint** conectado a este repositorio. Render leerá `render.yaml` y construirá la API.
4. Cuando Render lo solicite, configura `MONGODB_URI` con la cadena de conexión de Atlas y agrega `/franquicias` como nombre de la base. No guardes esa cadena en GitHub.
5. Cuando el despliegue termine, comprueba `https://TU-API.onrender.com/actuator/health`. Debe responder con `"status":"UP"`.

Render puede tardar cerca de un minuto en responder la primera vez después de estar inactivo en el plan gratuito. Atlas M0 es gratuito y tiene límites de almacenamiento y capacidad. La carpeta `terraform/` también contiene una alternativa para crear Atlas con código, pero requiere instalar Terraform y configurar credenciales de Atlas.
