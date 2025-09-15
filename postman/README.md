# Colección de Postman para Performance API

## Descripción

Esta colección de Postman contiene todos los endpoints de la API de rendimiento de Spring Boot, organizados de manera lógica para facilitar las pruebas manuales antes de ejecutar las pruebas automatizadas con JMeter.

## Archivos Incluidos

- `Performance-API-Collection.json` - Colección principal con todos los endpoints
- `Performance-API-Environment.json` - Variables de entorno para facilitar el uso
- `README.md` - Este archivo de documentación

## Cómo Importar

### 1. Importar la Colección
1. Abre Postman
2. Haz clic en "Import" en la esquina superior izquierda
3. Selecciona "Upload Files"
4. Navega a la carpeta `postman/` y selecciona `Performance-API-Collection.json`
5. Haz clic en "Import"

### 2. Importar el Entorno
1. En Postman, haz clic en el ícono de engranaje (Settings) en la esquina superior derecha
2. Selecciona "Manage Environments"
3. Haz clic en "Import"
4. Selecciona `Performance-API-Environment.json`
5. Haz clic en "Import"

### 3. Seleccionar el Entorno
1. En la esquina superior derecha, selecciona "Performance API Environment" del dropdown
2. Verifica que la variable `baseUrl` esté configurada como `http://localhost:8080`

## Estructura de la Colección

### 1. Users API
- **Get All Users** - Obtiene todos los usuarios (con problemas de rendimiento)
- **Get User by ID** - Obtiene un usuario específico
- **Create User** - Crea un nuevo usuario
- **Update User** - Actualiza un usuario existente
- **Delete User** - Elimina un usuario
- **Search Users** - Busca usuarios por palabra clave
- **Get Users with Pending Orders** - Obtiene usuarios con órdenes pendientes
- **Get Recent Users** - Obtiene usuarios recientes por dominio de email
- **Get Cached Users** - Obtiene usuarios en caché (puede causar memory leak)
- **Get All Users Async** - Obtiene usuarios de forma asíncrona
- **User Health Check** - Verifica el estado del servicio

### 2. Products API
- **Get All Products** - Obtiene todos los productos (con problemas de rendimiento)
- **Get Product by ID** - Obtiene un producto específico
- **Create Product** - Crea un nuevo producto
- **Update Product** - Actualiza un producto existente
- **Delete Product** - Elimina un producto
- **Search Products** - Busca productos por palabra clave
- **Get Products by Category** - Obtiene productos por categoría
- **Get Products by Price Range** - Obtiene productos por rango de precio
- **Get Available Products** - Obtiene productos disponibles
- **Get Products with Images** - Obtiene productos con imágenes (datos grandes)
- **Get Cached Products** - Obtiene productos en caché (puede causar memory leak)
- **Get All Products Async** - Obtiene productos de forma asíncrona
- **Product Health Check** - Verifica el estado del servicio

### 3. Orders API
- **Get All Orders** - Obtiene todas las órdenes (con problemas de rendimiento)
- **Get Order by ID** - Obtiene una orden específica
- **Create Order** - Crea una nueva orden
- **Update Order** - Actualiza una orden existente
- **Delete Order** - Elimina una orden
- **Get Orders by User ID** - Obtiene órdenes por ID de usuario
- **Get Orders by Status** - Obtiene órdenes por estado
- **Get Orders by Date Range** - Obtiene órdenes por rango de fechas
- **Get Orders by Min Amount** - Obtiene órdenes por monto mínimo
- **Get Orders with Notes** - Obtiene órdenes con notas (datos grandes)
- **Create Order with Items** - Crea una orden con items
- **Get Cached Orders** - Obtiene órdenes en caché (puede causar memory leak)
- **Get All Orders Async** - Obtiene órdenes de forma asíncrona
- **Order Health Check** - Verifica el estado del servicio

### 4. Health & Monitoring
- **Application Health** - Estado general de la aplicación
- **Application Info** - Información de la aplicación
- **Application Metrics** - Métricas de la aplicación
- **Prometheus Metrics** - Métricas en formato Prometheus
- **H2 Database Console** - Acceso a la consola de la base de datos

## Problemas de Rendimiento a Detectar

### 1. Problemas de Base de Datos
- **N+1 Queries**: Los endpoints que obtienen listas cargan datos relacionados innecesariamente
- **Consultas Ineficientes**: Algunos endpoints usan consultas complejas sin optimización
- **Falta de Paginación**: Todos los endpoints de listado cargan todos los registros

### 2. Problemas de Memoria
- **Memory Leaks**: Los endpoints de caché pueden causar memory leaks
- **Objetos Grandes**: Los endpoints con imágenes y notas cargan datos grandes innecesariamente
- **Creación Ineficiente**: Algunos endpoints crean objetos de manera ineficiente

### 3. Problemas de Concurrencia
- **Agotamiento de Recursos**: Los endpoints asíncronos pueden agotar el pool de threads
- **Race Conditions**: Los endpoints de caché pueden tener condiciones de carrera

## Cómo Usar

### 1. Pruebas Básicas
1. Asegúrate de que la aplicación Spring Boot esté ejecutándose
2. Ejecuta los endpoints de Health Check para verificar que la API esté funcionando
3. Prueba los endpoints básicos de CRUD para cada entidad

### 2. Pruebas de Rendimiento
1. Ejecuta los endpoints que cargan listas completas (Get All Users, Products, Orders)
2. Observa los tiempos de respuesta y el tamaño de las respuestas
3. Ejecuta los endpoints de caché varias veces para detectar memory leaks
4. Prueba los endpoints asíncronos para detectar problemas de concurrencia

### 3. Pruebas de Búsqueda
1. Usa diferentes palabras clave en los endpoints de búsqueda
2. Prueba los filtros por categoría, precio, fecha, etc.
3. Observa cómo los filtros afectan el rendimiento

### 4. Pruebas de Datos Grandes
1. Ejecuta los endpoints que cargan datos grandes (with-images, with-notes)
2. Observa el impacto en el rendimiento y el uso de memoria
3. Compara con los endpoints que no cargan datos grandes

## Variables de Entorno

La colección incluye las siguientes variables predefinidas:

- `baseUrl`: URL base de la API (http://localhost:8080)
- `userId`: ID de usuario para pruebas (1)
- `productId`: ID de producto para pruebas (1)
- `orderId`: ID de orden para pruebas (1)
- `searchKeyword`: Palabra clave para búsquedas (test)
- `category`: Categoría para filtros (Electronics)
- `minPrice`: Precio mínimo para filtros (10)
- `maxPrice`: Precio máximo para filtros (1000)
- `minAmount`: Monto mínimo para filtros (100)
- `emailDomain`: Dominio de email para filtros (example.com)

## Consejos para Pruebas

### 1. Monitoreo
- Usa las métricas de la aplicación para monitorear el rendimiento
- Observa el uso de memoria en el H2 console
- Revisa los logs de la aplicación para detectar errores

### 2. Pruebas Iterativas
- Ejecuta los mismos endpoints varias veces para detectar degradación
- Prueba con diferentes volúmenes de datos
- Varía los parámetros de búsqueda y filtros

### 3. Pruebas de Carga
- Usa Postman Runner para ejecutar múltiples requests
- Configura delays entre requests para simular carga real
- Monitorea el rendimiento durante las pruebas

## Solución de Problemas

### 1. Error de Conexión
- Verifica que la aplicación Spring Boot esté ejecutándose
- Confirma que el puerto 8080 esté disponible
- Revisa la configuración de la variable `baseUrl`

### 2. Errores de Base de Datos
- Accede al H2 console para verificar los datos
- Revisa los logs de la aplicación para errores de SQL
- Verifica que las tablas estén creadas correctamente

### 3. Problemas de Rendimiento
- Usa las métricas de la aplicación para identificar cuellos de botella
- Revisa los logs para detectar consultas lentas
- Monitorea el uso de memoria y CPU

## Próximos Pasos

Después de probar manualmente con Postman:

1. **Ejecuta las pruebas de JMeter** para análisis automatizado
2. **Revisa las métricas de rendimiento** en la aplicación
3. **Identifica los problemas de rendimiento** más críticos
4. **Implementa optimizaciones** basadas en los hallazgos
5. **Valida las mejoras** con nuevas pruebas

Esta colección de Postman es el primer paso en el proceso de testing de rendimiento, proporcionando una base sólida para entender el comportamiento de la API antes de proceder con las pruebas automatizadas más intensivas.
