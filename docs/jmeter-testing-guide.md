# JMeter Testing Guide

## Overview

This guide provides comprehensive instructions for using JMeter to test the Spring Boot API with intentionally introduced performance issues.

## Prerequisites

### 1. Software Requirements
- Java 17 or higher
- Apache JMeter 5.5 or higher
- Maven 3.6 or higher
- Git

### 2. Installation
```bash
# Install JMeter (Linux/Mac)
wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.5.tgz
tar -xzf apache-jmeter-5.5.tgz
export JMETER_HOME=/path/to/apache-jmeter-5.5

# Install JMeter (Windows)
# Download from https://jmeter.apache.org/download_jmeter.cgi
# Extract to C:\apache-jmeter-5.5
# Set JMETER_HOME=C:\apache-jmeter-5.5
```

## Test Plan Structure

### 1. Performance Test Plan (`performance-test-plan.jmx`)
- **Purpose**: Comprehensive performance testing
- **Thread Groups**: 2 (Load Test, Stress Test)
- **Duration**: 5-10 minutes
- **Threads**: 10-20

### 2. Data Driven Test Plan (`data-driven-tests.jmx`)
- **Purpose**: Test with external data sources
- **Data Sources**: CSV files, JDBC connections
- **Thread Groups**: 1
- **Duration**: Variable

## Test Scenarios

### 1. Basic Load Test
- **Threads**: 10
- **Ramp-up**: 30 seconds
- **Duration**: 5 minutes
- **Endpoints**: All CRUD operations

### 2. Stress Test
- **Threads**: 20
- **Ramp-up**: 60 seconds
- **Duration**: 10 minutes
- **Focus**: Memory-intensive operations

### 3. Spike Test
- **Threads**: 50
- **Ramp-up**: 30 seconds
- **Duration**: 5 minutes
- **Focus**: Sudden load increases

### 4. Data Driven Test
- **Threads**: 5
- **Ramp-up**: 10 seconds
- **Duration**: Variable
- **Focus**: External data integration

## JMeter Components Used

### 1. Logic Controllers
- **If Controller**: Conditional execution
- **Loop Controller**: Repeat operations
- **Random Controller**: Random endpoint selection
- **Switch Controller**: Multiple endpoint routing

### 2. Data Sources
- **CSV Data Set Config**: External test data
- **JDBC Data Source**: Database connections
- **User Defined Variables**: Dynamic values

### 3. Assertions
- **Response Assertion**: Status code validation
- **Duration Assertion**: Response time validation
- **Size Assertion**: Response size validation

### 4. Timers
- **Constant Timer**: Fixed delays
- **Random Timer**: Variable delays
- **Gaussian Timer**: Normal distribution delays

## Performance Issues to Detect

### 1. Database Performance Issues
- **N+1 Query Problems**: Multiple database calls
- **Missing Indexes**: Slow query execution
- **Inefficient JOINs**: Complex query performance
- **Large Result Sets**: Memory consumption

### 2. Memory Issues
- **Memory Leaks**: Growing memory usage
- **Large Object Retention**: Unnecessary data loading
- **Inefficient Object Creation**: CPU overhead

### 3. Concurrency Issues
- **Race Conditions**: Data inconsistency
- **Deadlocks**: Thread blocking
- **Thread Pool Exhaustion**: Resource limits

### 4. External Service Issues
- **Slow API Calls**: Timeout problems
- **Circuit Breaker Failures**: Service degradation
- **Connection Pool Exhaustion**: Resource limits

## Running Tests

### 1. Command Line Execution
```bash
# Basic test execution
jmeter -n -t jmeter-tests/performance-test-plan.jmx -l results.jtl

# With custom parameters
jmeter -n -t jmeter-tests/performance-test-plan.jmx -l results.jtl -JTHREADS=50 -JDURATION=600

# Generate HTML report
jmeter -n -t jmeter-tests/performance-test-plan.jmx -l results.jtl -e -o report
```

### 2. Using Scripts
```bash
# Run all tests
./scripts/run-tests.sh --all

# Run specific scenario
./scripts/run-tests.sh --scenario load

# Windows
scripts\run-tests.bat --all
scripts\run-tests.bat load
```

### 3. GUI Execution
```bash
# Start JMeter GUI
jmeter

# Open test plan
File -> Open -> jmeter-tests/performance-test-plan.jmx

# Run test
Run -> Start
```

## Test Data Preparation

### 1. CSV Data Files
- **Location**: `jmeter-tests/test-data/`
- **Format**: Comma-separated values
- **Headers**: Required for variable mapping

### 2. Database Data
- **Source**: H2 in-memory database
- **Tables**: users, products, orders, order_items
- **Sample Data**: Pre-loaded with test data

### 3. Dynamic Data Generation
- **Random Values**: Using JMeter functions
- **Date/Time**: Current timestamp generation
- **UUIDs**: Unique identifier generation

## Result Analysis

### 1. Key Metrics
- **Response Time**: Average, 95th percentile
- **Throughput**: Requests per second
- **Error Rate**: Percentage of failures
- **Memory Usage**: Heap utilization

### 2. JMeter Reports
- **Summary Report**: Basic statistics
- **HTML Report**: Detailed analysis
- **Graph Results**: Visual representation

### 3. Performance Bottlenecks
- **Slow Endpoints**: High response times
- **Memory Leaks**: Growing memory usage
- **Database Issues**: Query performance
- **Concurrency Problems**: Thread blocking

## Troubleshooting

### 1. Common Issues
- **Connection Refused**: API not running
- **Out of Memory**: Insufficient heap space
- **Timeout Errors**: Slow response times
- **Database Errors**: Connection issues

### 2. Solutions
- **Start API**: `mvn spring-boot:run`
- **Increase Heap**: `-Xmx2g -Xms1g`
- **Adjust Timeouts**: Increase timeout values
- **Check Database**: Verify H2 console

### 3. Debugging
- **Enable Debug Logging**: Set log level to DEBUG
- **Check JMeter Logs**: Review error messages
- **Monitor Resources**: CPU, memory, disk usage
- **Database Queries**: Check H2 console

## Best Practices

### 1. Test Design
- **Start Small**: Begin with low load
- **Gradual Increase**: Ramp up slowly
- **Monitor Resources**: Watch system metrics
- **Document Results**: Keep detailed records

### 2. Performance Optimization
- **Identify Bottlenecks**: Focus on slow areas
- **Optimize Queries**: Fix database issues
- **Implement Caching**: Reduce database load
- **Monitor Memory**: Prevent leaks

### 3. Continuous Testing
- **Regular Testing**: Schedule periodic tests
- **Automated Testing**: Integrate with CI/CD
- **Performance Regression**: Track improvements
- **Capacity Planning**: Plan for growth

## Advanced Features

### 1. Custom Functions
- **User Defined Functions**: Custom logic
- **BeanShell Scripting**: Advanced scripting
- **JavaScript**: Client-side logic

### 2. Distributed Testing
- **Remote Testing**: Multiple machines
- **Load Balancing**: Distribute load
- **Result Aggregation**: Combine results

### 3. Integration
- **CI/CD Integration**: Automated testing
- **Monitoring Tools**: Real-time metrics
- **Alerting**: Performance alerts

## Conclusion

This JMeter testing guide provides comprehensive instructions for testing the Spring Boot API and identifying performance issues. Follow the best practices and use the provided test plans to ensure optimal performance.
