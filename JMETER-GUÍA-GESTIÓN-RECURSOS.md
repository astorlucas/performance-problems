# Guía para Crear Plan de Pruebas JMeter Manualmente

## Pasos recomendados a seguir:

**Crear nuevo Test Plan**
**Configurar Variables de Usuario que necesites**
**Configurar HTTP Request Defaults que necesites**

1. **Agregar Thread Group:**
   - Configurar:
     - Number of Threads: `100`
     - Ramp-up Period: `30`
     - Loop Count: `50`

2. **Agregar HTTP Request 1:**"
   - Configurar:
     - Path: `/api/users`
     - Method: `GET`

3. **Agregar HTTP Request 2:**
   - Configurar:
     - Path: `/api/users/async`
     - Method: `GET`

4. **Agregar HTTP Request 3:**
   - Configurar:
     - Path: `/api/products/stress-test`
     - Method: `GET`

## Crear Thread Group 2 - Memory Exhaustion - Se recomienda hacerlo en un nuevo TesPlan para claridad

1. **Agregar Thread Group:**
   - Configurar:
     - Number of Threads: `50`
     - Ramp-up Period: `20`
     - Loop Count: `30`

2. **Agregar HTTP Request 1:**
   - Configurar:
     - Path: `/api/products/with-images`
     - Method: `GET`

3. **Agregar HTTP Request 2:**
   - Configurar:
     - Path: `/api/products/cached`
     - Method: `GET`

## Crear Thread Group 3 - Database Exhaustion - Se recomienda hacerlo en un nuevo TesPlan para claridad

1. **Agregar Thread Group:**
   - Configurar:
     - Number of Threads: `75`
     - Ramp-up Period: `30`
     - Loop Count: `100`

2. **Agregar HTTP Request 1:**
   - Configurar:
     - Path: `/api/users/pending-orders`
     - Method: `GET`

3. **Agregar HTTP Request 2:**
   - Configurar:
     - Path: `/api/users/search`
     - Method: `GET`
     - Parameters:
       - Name: `keyword`
       - Value: `test`

## Listeners

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

2. **Iniciar aplicación con límites de memoria - Esto puede o no funcionar, dependiendo de la configuración del ambiente donde están ejecutando la aplicación**
   ```bash
   # Windows
   set JAVA_OPTS=-Xms256m -Xmx512m -XX:MaxMetaspaceSize=128m
   mvn spring-boot:run
   ```

## Indicadores a analizar

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
