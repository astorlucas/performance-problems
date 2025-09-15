#!/bin/bash

# Performance Testing Script for Spring Boot API
# This script runs various JMeter test scenarios and generates reports

set -e

# Configuration
BASE_URL="http://localhost:8080"
JMETER_HOME="${JMETER_HOME:-/opt/apache-jmeter-5.5}"
RESULTS_DIR="results"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if JMeter is available
check_jmeter() {
    if [ ! -d "$JMETER_HOME" ]; then
        print_error "JMeter not found at $JMETER_HOME"
        print_error "Please set JMETER_HOME environment variable or install JMeter"
        exit 1
    fi
    
    if [ ! -f "$JMETER_HOME/bin/jmeter" ]; then
        print_error "JMeter executable not found at $JMETER_HOME/bin/jmeter"
        exit 1
    fi
    
    print_success "JMeter found at $JMETER_HOME"
}

# Function to check if API is running
check_api() {
    print_status "Checking if API is running at $BASE_URL..."
    
    if curl -s "$BASE_URL/api/users/health" > /dev/null; then
        print_success "API is running"
    else
        print_error "API is not running at $BASE_URL"
        print_error "Please start the Spring Boot application first"
        exit 1
    fi
}

# Function to create results directory
create_results_dir() {
    mkdir -p "$RESULTS_DIR/$TIMESTAMP"
    print_status "Results will be saved to $RESULTS_DIR/$TIMESTAMP"
}

# Function to run JMeter test
run_jmeter_test() {
    local test_name="$1"
    local test_file="$2"
    local output_file="$3"
    local log_file="$4"
    
    print_status "Running $test_name..."
    
    "$JMETER_HOME/bin/jmeter" \
        -n \
        -t "$test_file" \
        -l "$output_file" \
        -j "$log_file" \
        -e \
        -o "$RESULTS_DIR/$TIMESTAMP/$test_name-report"
    
    if [ $? -eq 0 ]; then
        print_success "$test_name completed successfully"
    else
        print_error "$test_name failed"
        return 1
    fi
}

# Function to run performance test
run_performance_test() {
    print_status "Starting Performance Test Suite..."
    
    # Basic Load Test
    run_jmeter_test "basic-load-test" \
        "jmeter-tests/performance-test-plan.jmx" \
        "$RESULTS_DIR/$TIMESTAMP/basic-load-results.jtl" \
        "$RESULTS_DIR/$TIMESTAMP/basic-load.log"
    
    # Data Driven Test
    run_jmeter_test "data-driven-test" \
        "jmeter-tests/data-driven-tests.jmx" \
        "$RESULTS_DIR/$TIMESTAMP/data-driven-results.jtl" \
        "$RESULTS_DIR/$TIMESTAMP/data-driven.log"
    
    # Stress Test with different parameters
    print_status "Running Stress Test with 50 threads..."
    "$JMETER_HOME/bin/jmeter" \
        -n \
        -t "jmeter-tests/performance-test-plan.jmx" \
        -l "$RESULTS_DIR/$TIMESTAMP/stress-test-results.jtl" \
        -j "$RESULTS_DIR/$TIMESTAMP/stress-test.log" \
        -JTHREADS=50 \
        -JRAMP_UP=60 \
        -JDURATION=600 \
        -e \
        -o "$RESULTS_DIR/$TIMESTAMP/stress-test-report"
    
    print_success "Stress test completed"
}

# Function to run specific test scenarios
run_scenario() {
    local scenario="$1"
    
    case "$scenario" in
        "load")
            print_status "Running Load Test..."
            run_jmeter_test "load-test" \
                "jmeter-tests/performance-test-plan.jmx" \
                "$RESULTS_DIR/$TIMESTAMP/load-test-results.jtl" \
                "$RESULTS_DIR/$TIMESTAMP/load-test.log"
            ;;
        "stress")
            print_status "Running Stress Test..."
            "$JMETER_HOME/bin/jmeter" \
                -n \
                -t "jmeter-tests/performance-test-plan.jmx" \
                -l "$RESULTS_DIR/$TIMESTAMP/stress-test-results.jtl" \
                -j "$RESULTS_DIR/$TIMESTAMP/stress-test.log" \
                -JTHREADS=100 \
                -JRAMP_UP=120 \
                -JDURATION=900 \
                -e \
                -o "$RESULTS_DIR/$TIMESTAMP/stress-test-report"
            ;;
        "spike")
            print_status "Running Spike Test..."
            "$JMETER_HOME/bin/jmeter" \
                -n \
                -t "jmeter-tests/performance-test-plan.jmx" \
                -l "$RESULTS_DIR/$TIMESTAMP/spike-test-results.jtl" \
                -j "$RESULTS_DIR/$TIMESTAMP/spike-test.log" \
                -JTHREADS=200 \
                -JRAMP_UP=30 \
                -JDURATION=300 \
                -e \
                -o "$RESULTS_DIR/$TIMESTAMP/spike-test-report"
            ;;
        "data-driven")
            print_status "Running Data Driven Test..."
            run_jmeter_test "data-driven-test" \
                "jmeter-tests/data-driven-tests.jmx" \
                "$RESULTS_DIR/$TIMESTAMP/data-driven-results.jtl" \
                "$RESULTS_DIR/$TIMESTAMP/data-driven.log"
            ;;
        *)
            print_error "Unknown scenario: $scenario"
            print_status "Available scenarios: load, stress, spike, data-driven"
            exit 1
            ;;
    esac
}

# Function to analyze results
analyze_results() {
    print_status "Analyzing test results..."
    
    local results_dir="$RESULTS_DIR/$TIMESTAMP"
    
    # Generate summary report
    "$JMETER_HOME/bin/jmeter" \
        -g "$results_dir/basic-load-results.jtl" \
        -o "$results_dir/summary-report"
    
    # Extract key metrics
    print_status "Key Performance Metrics:"
    echo "=================================="
    
    # Count total requests
    local total_requests=$(grep -c "200" "$results_dir/basic-load-results.jtl" 2>/dev/null || echo "0")
    echo "Total Successful Requests: $total_requests"
    
    # Calculate average response time
    local avg_response_time=$(awk -F',' 'NR>1 {sum+=$2; count++} END {if(count>0) print sum/count; else print "0"}' "$results_dir/basic-load-results.jtl" 2>/dev/null || echo "0")
    echo "Average Response Time: ${avg_response_time}ms"
    
    # Count errors
    local error_count=$(grep -c -v "200" "$results_dir/basic-load-results.jtl" 2>/dev/null || echo "0")
    echo "Error Count: $error_count"
    
    # Calculate error rate
    local total_count=$((total_requests + error_count))
    if [ $total_count -gt 0 ]; then
        local error_rate=$((error_count * 100 / total_count))
        echo "Error Rate: ${error_rate}%"
    else
        echo "Error Rate: 0%"
    fi
    
    print_success "Results analysis completed"
}

# Function to show help
show_help() {
    echo "Performance Testing Script for Spring Boot API"
    echo ""
    echo "Usage: $0 [OPTIONS] [SCENARIO]"
    echo ""
    echo "Options:"
    echo "  -h, --help     Show this help message"
    echo "  -a, --all      Run all test scenarios"
    echo "  -s, --scenario Run specific test scenario"
    echo ""
    echo "Scenarios:"
    echo "  load          Basic load test (10 threads, 5 minutes)"
    echo "  stress        Stress test (100 threads, 15 minutes)"
    echo "  spike         Spike test (200 threads, 5 minutes)"
    echo "  data-driven   Data-driven test with CSV and JDBC"
    echo ""
    echo "Examples:"
    echo "  $0 --all                    # Run all test scenarios"
    echo "  $0 --scenario load          # Run load test only"
    echo "  $0 load                     # Run load test only"
    echo ""
    echo "Environment Variables:"
    echo "  JMETER_HOME   Path to JMeter installation (default: /opt/apache-jmeter-5.5)"
    echo "  BASE_URL      API base URL (default: http://localhost:8080)"
}

# Main execution
main() {
    # Parse command line arguments
    case "$1" in
        -h|--help)
            show_help
            exit 0
            ;;
        -a|--all)
            check_jmeter
            check_api
            create_results_dir
            run_performance_test
            analyze_results
            ;;
        -s|--scenario)
            if [ -z "$2" ]; then
                print_error "Scenario not specified"
                show_help
                exit 1
            fi
            check_jmeter
            check_api
            create_results_dir
            run_scenario "$2"
            analyze_results
            ;;
        load|stress|spike|data-driven)
            check_jmeter
            check_api
            create_results_dir
            run_scenario "$1"
            analyze_results
            ;;
        "")
            print_error "No arguments provided"
            show_help
            exit 1
            ;;
        *)
            print_error "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"
