# Performance Testing Project + JMeter

## Project Structure
```
performance-problems/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── performance/
│   │   │           └── api/
│   │   │               ├── PerformanceApiApplication.java
│   │   │               ├── controller/
│   │   │               ├── service/
│   │   │               ├── repository/
│   │   │               ├── entity/
│   │   │               └── config/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── data.sql
├── jmeter-tests/
│   ├── performance-test-plan.jmx
│   ├── data-driven-tests.jmx
│   ├── load-test-scenarios.jmx
│   └── test-data/
├── docs/
│   ├── performance-analysis.md
│   ├── sli-slo-sla-definitions.md
│   └── jmeter-testing-guide.md
└── scripts/
    ├── run-tests.sh
    └── analyze-results.sh
```

### Running the Application
```bash
# Clone the repository
git clone <repository-url>
cd performance-problems

# Run the Spring Boot application
mvn spring-boot:run

# The API will be available at http://localhost:8080
```

## Contexto y motivación

La aplicación de ejemplo es un proyecto **Spring Boot REST** para manejar usuarios, productos y pedidos. 
Se incluyen deliberadamente **problemas de rendimiento**: consultas **N+1** en la base de datos, ausencia de índices, filtrados ineficientes, **fugas de memoria**, agotamiento de **hilos** o **conexiones** y **llamadas a servicios externos** con latencia artificial.

Se busca que quienes la utilicen puedan verificar el comportamiento de un sistema bajo carga. 

Este proyecto se centra en validar estar variantes de Testing de Performance:

- **Load testing**: Verifica si la aplicación soporta la carga de usuarios prevista.
- **Stress testing**: Somete al sistema a condiciones extremas para identificar su punto de quiebre.
- **Endurance testing**: Mantiene una carga estable durante un período prolongado para detectar fugas de recursos.
- **Spike testing**: Introduce picos de usuarios súbitos y mide la reacción de la aplicación.
- **Volume testing**: Genera grandes volúmenes de datos para comprobar la eficiencia de la base de datos.
- **Scalability testing**: Evalúa la capacidad de escalar ante un aumento gradual de usuarios.

Todo esto se integra con el concepto de **Site Reliability Engineering (SRE)** que aportará métricas y objetivos de fiabilidad. En SRE, los **SLA** fijan la fiabilidad requerida y los **SLI/SLO** se emplean para medirla y alcanzarla. Estas definiciones se incorporan al final del proyecto para elaborar indicadores y objetivos acordes a los resultados de las pruebas.

## Hitos

---

### Hito 1 – Semana 1 – Exploración y línea base

**Objetivo:** Familiarizarse con la aplicación, levantarla en el entorno local, descubrir sus endpoints REST, establecer un flujo típico de usuario y obtener una línea base de rendimiento.

**Exploración de endpoints**  
Usar herramientas como **Postman** para listar usuarios, productos y pedidos. Documentar entradas y salidas de cada endpoint.

**Diseño de flujo de usuario**  
Definir un recorrido típico (no es el único) por ejemplo, **obtener lista de productos → consultar detalle → crear pedido**. Este flujo servirá de base para las pruebas.

**Primer plan JMeter**  
Construir un Test Plan simple en JMeter con un **Thread Group** de pocos usuarios (p. ej., 5 hilos) que ejecute el flujo de usuario. Configurar **HTTP Request Defaults**, **HTTP Header Manager** y un **Loop Controller** si se requiere repetir acciones internas sin duplicar nodos.

**Ejecución y línea base**  
Correr el plan para obtener **tiempos de respuesta promedio**, **percentil 95** y **tasa de errores**. Registrar el entorno de pruebas (CPU, memoria, versiones) y los resultados. Esta **línea base** es esencial para comparar mejoras posteriores.

**Información que deberás tener al finalizar este hito**  
Listado completo de **endpoints** y parámetros, detalles de **autenticación** si existiera (tokens/keys), configuración por defecto de la base **H2** (URL JDBC, credenciales) y dependencias **externas** de la aplicación (si ciertos endpoints dependen de servicios externos).

---

### Hito 2 – Semana 2 – Pruebas dirigidas e identificación de issues

**Objetivo:** Crear varios planes de prueba para detectar los problemas de rendimiento introducidos intencionalmente en el proyecto. Cada problema se aborda con un escenario específico en JMeter.

**Pruebas de base de datos**  
Diseñar planes que consulten listados con gran cantidad de registros para revelar los problemas. Usar **Peticiones REST** que desencadenen estas consultas. Observar y concluír.

**Pruebas de memoria**  
Ejecutar un test de **endurance** con muchos hilos y **larga duración** usando **Loop Controller** y timers de **think time** para revelar los problemas. Monitorear **memoria** y **CPU** (con administrador de tareas).

**Pruebas de concurrencia**  
Crear un **Stress Test** que aumente progresivamente el número de hilos hasta saturar el **thread pool** o el **pool de conexiones**. Configurar **Thread Groups** con **ramp-up** y límites altos de usuarios.

**Gestión de recursos**  
Probar la saturación de **conexiones** (memoria heap) estableciendo y cerrando conexiones podemos replicar este comportamiento, y, por supuesto observando los errores de agotamiento (**exhaustion**).
- Se proporciona un archivo dentro del respositorio, llamado: JMETER-GUÍA-GESTIÓN-RECURSOS.md el objetivo de este es proporcionar un paso a paso para generar los TestPlan correspondientes a este punto.

**Servicios externos**  
Usar un **Constant Timer** para simular **latencia alta** en llamadas a servicios externos, y configurar **Connection Timeout / Response Timeout** en JMeter para provocar **timeouts** controlados.

**Uso de controladores lógicos**  
Emplear **If Controllers** para ejecutar peticiones condicionales, **Random Controllers** para generar variedad en los escenarios y **Switch Controllers** para seleccionar rutas basadas en variables. Integrar **CSV Data Set Config** para pruebas **data‑driven** y **Assertions** para validar códigos de estado y **tiempos máximos**.

**Registro de resultados**  
Después de cada prueba, analizar los **reportes HTML** generados por JMeter y anotar los patrones observados.

**Información que deberás tener al finalizar este hito**  
Detalles de la **implementación** de las fugas de memoria o del **punto de quiebre** del thread pool para ajustar las pruebas, la configuración de los parámetros del **pool de conexiones** a la base de datos y acceso a **logs** de la aplicación para correlacionar con los tiempos de JMeter.

---

### Hito 3 – Semana 3 – Análisis, SLI/SLO/SLA e informe final

**Objetivo:** Interpretar los datos de las pruebas, identificar cuellos de botella, proponer mejoras y establecer métricas de fiabilidad.

**Análisis detallado**  
Consolidar los resultados de las pruebas (archivos **JTL** y reportes) y calcular métricas clave: **latencia media**, **p95**, **p99**, **throughput** (solicitudes por segundo), **tasa de errores**, **utilización de CPU y memoria**. Identificar en qué pruebas surgen tiempos altos o errores y correlacionar con los recursos monitoreados.

**Identificación de cuellos de botella**  
Basándose en los datos, detectar si el problema principal es de **consultas**, de **memoria** (fugas), de **concurrencia** o de **recursos externos**.

**Definición de SLI**  
Establecer qué indicadores se medirán: *Ttiempos de respuesta** (mediana, p95, p99), **throughput**, **porcentaje de errores** y **disponibilidad**.

**Definición de SLO**  
Fijar objetivos numéricos para cada SLI (p. ej., **p95 < 500 ms** para listados de usuarios, **error rate < 1 %** para creación de pedidos).

**Definición de SLA**  
Redactar un **Acuerdo de Nivel de Servicio** indicando compromisos aceptados (por ejemplo, **99.9 %** de disponibilidad, **tiempos de respuesta** máximos y **tiempos de recuperación** para incidentes críticos). En la práctica de SRE, los SLA describen la fiabilidad requerida, mientras que los SLI y SLO son las herramientas para alcanzarla.

**Propuestas de optimización**  
Sugerir cambios en la aplicación: Añadir **índices**, reescribir **consultas**, emplear **caches**, ajustar tamaños de **pools** de hilos/conexiones o manejar mejor las **excepciones de timeout** y **reintentos**.

**Informe final**  
Elaborar un documento detallado con **contexto**, **metodología de prueba**, **configuraciones de JMeter**, **resultados numéricos**, **interpretación**, **SLI/SLO/SLA definidos**, **mejoras posibles** y **conclusiones**. Incluir **gráficas y tablas** de JMeter para ilustrar los hallazgos.

**Información que deberás tener al finalizar este hito**  
Una explicación de cómo los **problemas de rendimiento** fueron implementados (por ejemplo, qué repositorio presenta N+1, cómo se provoca la fuga de memoria), para proponer mejoras específicas.

---

## Guía general y buenas prácticas

- **Define tu entorno:** Usa siempre la misma configuración de hardware/software para que los resultados sean comparables. Si cambias la configuración (por ejemplo, heap de la JVM o tamaño del connection/thread pool), anótalo en los reportes.
- **Datos consistentes:** Antes de cada prueba, reinicia la aplicación y restablece la base de datos para que los datos sean comparables. Las pruebas que insertan o modifican registros deben ejecutarse sobre un entorno controlado.
- **Controla la carga:** Fija claramente **número de hilos**, **ramp‑up** y **duración** de cada test. Un aumento descontrolado de hilos puede esconder las causas verdaderas de un problema al generar errores en cascada.
- **Assertions y fallos:** Configura **Assertions** en JMeter para detectar respuestas fuera de los límites de tus SLO. Un test que excede el tiempo máximo es **fallo** aunque devuelva código **200**.
- **Uso de CLI y versionado:** Ejecuta JMeter en modo **no gráfico** (`-n`) y registra los **.jtl** generados. **Versiona** junto con los **.jmx** y scripts de ejecución para reproducibilidad.
- **Consistencia de las métricas:** Interpreta **latencia + throughput + tasa de errores** en conjunto.
- **Documenta todo:** registra cada decisión (por qué tantos hilos, por qué se activó un timer, etc.). Facilita el análisis y evita repetir errores.

---


