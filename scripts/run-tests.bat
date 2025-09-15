@echo off
REM Performance Testing Script for Spring Boot API
REM This script runs various JMeter test scenarios and generates reports

setlocal enabledelayedexpansion

REM Configuration
set BASE_URL=http://localhost:8080
set JMETER_HOME=%JMETER_HOME%
if "%JMETER_HOME%"=="" set JMETER_HOME=C:\apache-jmeter-5.5
set RESULTS_DIR=results
for /f "tokens=2 delims==" %%a in ('wmic OS Get localdatetime /value') do set "dt=%%a"
set TIMESTAMP=%dt:~0,8%_%dt:~8,6%

REM Function to print status
:print_status
echo [INFO] %~1
goto :eof

:print_success
echo [SUCCESS] %~1
goto :eof

:print_warning
echo [WARNING] %~1
goto :eof

:print_error
echo [ERROR] %~1
goto :eof

REM Function to check if JMeter is available
:check_jmeter
if not exist "%JMETER_HOME%" (
    call :print_error "JMeter not found at %JMETER_HOME%"
    call :print_error "Please set JMETER_HOME environment variable or install JMeter"
    exit /b 1
)

if not exist "%JMETER_HOME%\bin\jmeter.bat" (
    call :print_error "JMeter executable not found at %JMETER_HOME%\bin\jmeter.bat"
    exit /b 1
)

call :print_success "JMeter found at %JMETER_HOME%"
goto :eof

REM Function to check if API is running
:check_api
call :print_status "Checking if API is running at %BASE_URL%..."

curl -s "%BASE_URL%/api/users/health" >nul 2>&1
if %errorlevel% neq 0 (
    call :print_error "API is not running at %BASE_URL%"
    call :print_error "Please start the Spring Boot application first"
    exit /b 1
)

call :print_success "API is running"
goto :eof

REM Function to create results directory
:create_results_dir
if not exist "%RESULTS_DIR%" mkdir "%RESULTS_DIR%"
if not exist "%RESULTS_DIR%\%TIMESTAMP%" mkdir "%RESULTS_DIR%\%TIMESTAMP%"
call :print_status "Results will be saved to %RESULTS_DIR%\%TIMESTAMP%"
goto :eof

REM Function to run JMeter test
:run_jmeter_test
set test_name=%~1
set test_file=%~2
set output_file=%~3
set log_file=%~4

call :print_status "Running %test_name%..."

"%JMETER_HOME%\bin\jmeter.bat" ^
    -n ^
    -t "%test_file%" ^
    -l "%output_file%" ^
    -j "%log_file%" ^
    -e ^
    -o "%RESULTS_DIR%\%TIMESTAMP%\%test_name%-report"

if %errorlevel% neq 0 (
    call :print_error "%test_name% failed"
    exit /b 1
)

call :print_success "%test_name% completed successfully"
goto :eof

REM Function to run performance test
:run_performance_test
call :print_status "Starting Performance Test Suite..."

REM Basic Load Test
call :run_jmeter_test "basic-load-test" "jmeter-tests\performance-test-plan.jmx" "%RESULTS_DIR%\%TIMESTAMP%\basic-load-results.jtl" "%RESULTS_DIR%\%TIMESTAMP%\basic-load.log"

REM Data Driven Test
call :run_jmeter_test "data-driven-test" "jmeter-tests\data-driven-tests.jmx" "%RESULTS_DIR%\%TIMESTAMP%\data-driven-results.jtl" "%RESULTS_DIR%\%TIMESTAMP%\data-driven.log"

REM Stress Test with different parameters
call :print_status "Running Stress Test with 50 threads..."
"%JMETER_HOME%\bin\jmeter.bat" ^
    -n ^
    -t "jmeter-tests\performance-test-plan.jmx" ^
    -l "%RESULTS_DIR%\%TIMESTAMP%\stress-test-results.jtl" ^
    -j "%RESULTS_DIR%\%TIMESTAMP%\stress-test.log" ^
    -JTHREADS=50 ^
    -JRAMP_UP=60 ^
    -JDURATION=600 ^
    -e ^
    -o "%RESULTS_DIR%\%TIMESTAMP%\stress-test-report"

if %errorlevel% neq 0 (
    call :print_error "Stress test failed"
    exit /b 1
)

call :print_success "Stress test completed"
goto :eof

REM Function to run specific test scenarios
:run_scenario
set scenario=%~1

if "%scenario%"=="load" (
    call :print_status "Running Load Test..."
    call :run_jmeter_test "load-test" "jmeter-tests\performance-test-plan.jmx" "%RESULTS_DIR%\%TIMESTAMP%\load-test-results.jtl" "%RESULTS_DIR%\%TIMESTAMP%\load-test.log"
) else if "%scenario%"=="stress" (
    call :print_status "Running Stress Test..."
    "%JMETER_HOME%\bin\jmeter.bat" ^
        -n ^
        -t "jmeter-tests\performance-test-plan.jmx" ^
        -l "%RESULTS_DIR%\%TIMESTAMP%\stress-test-results.jtl" ^
        -j "%RESULTS_DIR%\%TIMESTAMP%\stress-test.log" ^
        -JTHREADS=100 ^
        -JRAMP_UP=120 ^
        -JDURATION=900 ^
        -e ^
        -o "%RESULTS_DIR%\%TIMESTAMP%\stress-test-report"
) else if "%scenario%"=="spike" (
    call :print_status "Running Spike Test..."
    "%JMETER_HOME%\bin\jmeter.bat" ^
        -n ^
        -t "jmeter-tests\performance-test-plan.jmx" ^
        -l "%RESULTS_DIR%\%TIMESTAMP%\spike-test-results.jtl" ^
        -j "%RESULTS_DIR%\%TIMESTAMP%\spike-test.log" ^
        -JTHREADS=200 ^
        -JRAMP_UP=30 ^
        -JDURATION=300 ^
        -e ^
        -o "%RESULTS_DIR%\%TIMESTAMP%\spike-test-report"
) else if "%scenario%"=="data-driven" (
    call :print_status "Running Data Driven Test..."
    call :run_jmeter_test "data-driven-test" "jmeter-tests\data-driven-tests.jmx" "%RESULTS_DIR%\%TIMESTAMP%\data-driven-results.jtl" "%RESULTS_DIR%\%TIMESTAMP%\data-driven.log"
) else (
    call :print_error "Unknown scenario: %scenario%"
    call :print_status "Available scenarios: load, stress, spike, data-driven"
    exit /b 1
)
goto :eof

REM Function to analyze results
:analyze_results
call :print_status "Analyzing test results..."

set results_dir=%RESULTS_DIR%\%TIMESTAMP%

REM Generate summary report
"%JMETER_HOME%\bin\jmeter.bat" ^
    -g "%results_dir%\basic-load-results.jtl" ^
    -o "%results_dir%\summary-report"

REM Extract key metrics
call :print_status "Key Performance Metrics:"
echo ==================================

REM Count total requests
for /f %%i in ('findstr /c:"200" "%results_dir%\basic-load-results.jtl" 2^>nul ^| find /c /v ""') do set total_requests=%%i
if "%total_requests%"=="" set total_requests=0
echo Total Successful Requests: %total_requests%

REM Count errors
for /f %%i in ('findstr /v /c:"200" "%results_dir%\basic-load-results.jtl" 2^>nul ^| find /c /v ""') do set error_count=%%i
if "%error_count%"=="" set error_count=0
echo Error Count: %error_count%

REM Calculate error rate
set /a total_count=%total_requests% + %error_count%
if %total_count% gtr 0 (
    set /a error_rate=%error_count% * 100 / %total_count%
    echo Error Rate: %error_rate%%%
) else (
    echo Error Rate: 0%%
)

call :print_success "Results analysis completed"
goto :eof

REM Function to show help
:show_help
echo Performance Testing Script for Spring Boot API
echo.
echo Usage: %0 [OPTIONS] [SCENARIO]
echo.
echo Options:
echo   -h, --help     Show this help message
echo   -a, --all      Run all test scenarios
echo   -s, --scenario Run specific test scenario
echo.
echo Scenarios:
echo   load          Basic load test (10 threads, 5 minutes)
echo   stress        Stress test (100 threads, 15 minutes)
echo   spike         Spike test (200 threads, 5 minutes)
echo   data-driven   Data-driven test with CSV and JDBC
echo.
echo Examples:
echo   %0 --all                    # Run all test scenarios
echo   %0 --scenario load          # Run load test only
echo   %0 load                     # Run load test only
echo.
echo Environment Variables:
echo   JMETER_HOME   Path to JMeter installation (default: C:\apache-jmeter-5.5)
echo   BASE_URL      API base URL (default: http://localhost:8080)
goto :eof

REM Main execution
:main
if "%1"=="-h" goto :show_help
if "%1"=="--help" goto :show_help
if "%1"=="-a" goto :run_all
if "%1"=="--all" goto :run_all
if "%1"=="-s" goto :run_scenario_specific
if "%1"=="--scenario" goto :run_scenario_specific
if "%1"=="load" goto :run_scenario_direct
if "%1"=="stress" goto :run_scenario_direct
if "%1"=="spike" goto :run_scenario_direct
if "%1"=="data-driven" goto :run_scenario_direct
if "%1"=="" goto :show_help
goto :show_help

:run_all
call :check_jmeter
if %errorlevel% neq 0 exit /b 1
call :check_api
if %errorlevel% neq 0 exit /b 1
call :create_results_dir
call :run_performance_test
call :analyze_results
goto :eof

:run_scenario_specific
if "%2"=="" (
    call :print_error "Scenario not specified"
    goto :show_help
)
call :check_jmeter
if %errorlevel% neq 0 exit /b 1
call :check_api
if %errorlevel% neq 0 exit /b 1
call :create_results_dir
call :run_scenario "%2"
call :analyze_results
goto :eof

:run_scenario_direct
call :check_jmeter
if %errorlevel% neq 0 exit /b 1
call :check_api
if %errorlevel% neq 0 exit /b 1
call :create_results_dir
call :run_scenario "%1"
call :analyze_results
goto :eof

REM Run main function with all arguments
call :main %*
