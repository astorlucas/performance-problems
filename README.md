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

## Hitos