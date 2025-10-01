Estos son cambios que estoy haciendo para probar la aprobación sobre el nuevo branch recientemente creado.

# Guía para Crear Plan de Pruebas JMeter Manualmente

Dado que el archivo XML está causando problemas de compatibilidad, te proporciono una guía paso a paso para crear el plan de pruebas manualmente en JMeter 5.6.3.

## Paso 1: Crear el Plan de Pruebas Base

1. **Abrir JMeter 5.6.3**
2. **Crear nuevo Test Plan:**
   - File → New
   - Renombrar el Test Plan a "Resource Exhaustion Test Plan"

## Paso 2: Configurar Variables de Usuario

1. **Agregar User Defined Variables:**
   - Click derecho en Test Plan → Add → Config Element → User Defined Variables
   - Agregar variable:
     - Name: `BASE_URL`
     - Value: `http://localhost:8080`

## Paso 3: Configurar HTTP Request Defaults

1. **Agregar HTTP Request Defaults:**
   - Click derecho en Test Plan → Add → Config Element → HTTP Request Defaults
   - Configurar:
     - Server Name or IP: `${BASE_URL}`
     - Port Number: (dejar vacío)
     - Protocol: `http`
     - Connection Timeout: `30000`
     - Response Timeout: `60000`

## Paso 4: Crear Thread Group 1 - HTTP Connection Exhaustion

1. **Agregar Thread Group:**
   - Click derecho en Test Plan → Add → Threads (Users) → Thread Group
   - Nombre: "HTTP Connection Exhaustion Test"
   - Configurar:
     - Number of Threads: `100`
     - Ramp-up Period: `30`
     - Loop Count: `50`

2. **Agregar HTTP Request 1:**
   - Click derecho en Thread Group → Add → Sampler → HTTP Request
   - Nombre: "Get All Users"
   - Configurar:
     - Path: `/api/users`
     - Method: `GET`

3. **Agregar HTTP Request 2:**
   - Click derecho en Thread Group → Add → Sampler → HTTP Request
   - Nombre: "Get Users Async"
   - Configurar:
     - Path: `/api/users/async`
     - Method: `GET`

4. **Agregar HTTP Request 3:**
   - Click derecho en Thread Group → Add → Sampler → HTTP Request
   - Nombre: "Product Stress Test"
   - Configurar:
     - Path: `/api/products/stress-test`
     - Method: `GET`

## Paso 5: Crear Thread Group 2 - Memory Exhaustion

1. **Agregar Thread Group:**
   - Click derecho en Test Plan → Add → Threads (Users) → Thread Group
   - Nombre: "Memory Exhaustion Test"
   - Configurar:
     - Number of Threads: `50`
     - Ramp-up Period: `20`
     - Loop Count: `30`

2. **Agregar HTTP Request 1:**
   - Click derecho en Thread Group → Add → Sampler → HTTP Request
   - Nombre: "Get Products with Images"
   - Configurar:
     - Path: `/api/products/with-images`
     - Method: `GET`

3. **Agregar HTTP Request 2:**
   - Click derecho en Thread Group → Add → Sampler → HTTP Request
   - Nombre: "Get Cached Products"
   - Configurar:
     - Path: `/api/products/cached`
     - Method: `GET`

## Paso 6: Crear Thread Group 3 - Database Exhaustion

1. **Agregar Thread Group:**
   - Click derecho en Test Plan → Add → Threads (Users) → Thread Group
   - Nombre: "Database Exhaustion Test"
   - Configurar:
     - Number of Threads: `75`
     - Ramp-up Period: `30`
     - Loop Count: `100`

2. **Agregar HTTP Request 1:**
   - Click derecho en Thread Group → Add → Sampler → HTTP Request
   - Nombre: "Get Users with Pending Orders"
   - Configurar:
     - Path: `/api/users/pending-orders`
     - Method: `GET`

3. **Agregar HTTP Request 2:**
   - Click derecho en Thread Group → Add → Sampler → HTTP Request
   - Nombre: "Search Users"
   - Configurar:
     - Path: `/api/users/search`
     - Method: `GET`
     - Parameters:
       - Name: `keyword`
       - Value: `test`

## Paso 7: Agregar Listeners para Monitoreo

1. **Agregar View Results Tree:**
   - Click derecho en Test Plan → Add → Listener → View Results Tree
   - Nombre: "View Results Tree"

2. **Agregar Summary Report:**
   - Click derecho en Test Plan → Add → Listener → Summary Report
   - Nombre: "Summary Report"

3. **Agregar Response Times Over Time:**
   - Click derecho en Test Plan → Add → Listener → Response Times Over Time
   - Nombre: "Response Times Over Time"

## Paso 8: Configurar la Aplicación para Pruebas

1. **Usar configuración con límites restrictivos:**
   ```bash
   # Copiar la configuración de recursos
   cp src/main/resources/application-resource-test.yml src/main/resources/application.yml
   ```

2. **Iniciar aplicación con límites de memoria:**
   ```bash
   # Windows
   set JAVA_OPTS=-Xms256m -Xmx512m -XX:MaxMetaspaceSize=128m
   mvn spring-boot:run

   # Linux
   export JAVA_OPTS="-Xms256m -Xmx512m -XX:MaxMetaspaceSize=128m"
   mvn spring-boot:run
   ```

## Paso 9: Ejecutar las Pruebas

1. **Verificar que la aplicación esté corriendo** en http://localhost:8080
2. **Ejecutar el plan de pruebas** en JMeter
3. **Monitorear los resultados** en los listeners

## Endpoints que Causan Agotamiento de Recursos

### HTTP Connection Exhaustion:
- `/api/users` - Carga masiva sin paginación
- `/api/users/async` - Agota pool de threads
- `/api/products/stress-test` - Consume CPU y memoria

### Memory Exhaustion:
- `/api/products/with-images` - Carga imágenes (memoria)
- `/api/products/cached` - Cache que crece indefinidamente

### Database Exhaustion:
- `/api/users/pending-orders` - N+1 queries
- `/api/users/search` - Múltiples queries

## Indicadores de Agotamiento

### File Handles:
- Error: "Too many open files"
- Comando: `netstat -an | find /c "ESTABLISHED"` (Windows)

### Conexiones de BD:
- Error: "Connection pool exhausted"
- Log: "HikariPool-1 - Connection is not available"

### Memoria:
- Error: "OutOfMemoryError"
- Aplicación se cuelga o reinicia

### Threads:
- Error: "Thread pool exhausted"
- Log: "Tomcat thread pool is full"

## Guardar el Plan de Pruebas

1. **File → Save Test Plan As...**
2. **Guardar como:** `ResourceExhaustionTest.jmx`
3. **El archivo se guardará con la estructura correcta**

Esta guía te permitirá crear un plan de pruebas funcional que no tendrá problemas de compatibilidad con JMeter 5.6.3.
