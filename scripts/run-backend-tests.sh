#!/bin/bash
# Backend Test Runner Script
# Runs unit tests for all backend services and collects results

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
BACKEND_DIR="$PROJECT_ROOT/backend"

# Results directory
RESULTS_DIR="$PROJECT_ROOT/test-results"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
RUN_DIR="$RESULTS_DIR/$TIMESTAMP"

# Enforce Java 21
export JAVA_HOME="/c/Program Files/Java/jdk-21"
export PATH="$JAVA_HOME/bin:$PATH"
echo "Using Java from: $JAVA_HOME"
java -version

# Services to test
SERVICES=("user-service" "quiz-service" "progress-service")

# Initialize
echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}  Pilot Quiz Backend Test Runner${NC}"
echo -e "${YELLOW}========================================${NC}"
echo ""

# Create results directory
mkdir -p "$RUN_DIR"
echo -e "Results will be saved to: ${GREEN}$RUN_DIR${NC}"
echo ""

# Summary tracking
TOTAL_TESTS=0
TOTAL_PASSED=0
TOTAL_FAILED=0
FAILED_SERVICES=()

# Run tests for each service
for SERVICE in "${SERVICES[@]}"; do
    SERVICE_DIR="$BACKEND_DIR/$SERVICE"
    
    if [ ! -d "$SERVICE_DIR" ]; then
        echo -e "${RED}Service directory not found: $SERVICE_DIR${NC}"
        continue
    fi
    
    echo -e "${YELLOW}Testing: $SERVICE${NC}"
    echo "----------------------------------------"
    
    cd "$SERVICE_DIR"
    
    # Run Maven tests and capture output
    TEST_OUTPUT="$RUN_DIR/${SERVICE}-test-output.txt"
    
    if mvn test -B 2>&1 | tee "$TEST_OUTPUT"; then
        echo -e "${GREEN}✓ $SERVICE tests passed${NC}"
    else
        echo -e "${RED}✗ $SERVICE tests failed${NC}"
        FAILED_SERVICES+=("$SERVICE")
    fi
    
    # Copy test reports
    if [ -d "target/surefire-reports" ]; then
        mkdir -p "$RUN_DIR/$SERVICE"
        cp -r target/surefire-reports/* "$RUN_DIR/$SERVICE/"
    fi
    
    # Copy JaCoCo coverage report if exists
    if [ -d "target/site/jacoco" ]; then
        mkdir -p "$RUN_DIR/$SERVICE/coverage"
        cp -r target/site/jacoco/* "$RUN_DIR/$SERVICE/coverage/"
    fi
    
    # Extract test counts from output
    TESTS_RUN=$(grep -oP 'Tests run: \K\d+' "$TEST_OUTPUT" | tail -1 || echo "0")
    FAILURES=$(grep -oP 'Failures: \K\d+' "$TEST_OUTPUT" | tail -1 || echo "0")
    ERRORS=$(grep -oP 'Errors: \K\d+' "$TEST_OUTPUT" | tail -1 || echo "0")
    
    PASSED=$((TESTS_RUN - FAILURES - ERRORS))
    TOTAL_TESTS=$((TOTAL_TESTS + TESTS_RUN))
    TOTAL_PASSED=$((TOTAL_PASSED + PASSED))
    TOTAL_FAILED=$((TOTAL_FAILED + FAILURES + ERRORS))
    
    echo ""
done

# Generate summary report
SUMMARY_FILE="$RUN_DIR/summary.txt"
echo "====================================" > "$SUMMARY_FILE"
echo " Test Run Summary" >> "$SUMMARY_FILE"
echo " $(date)" >> "$SUMMARY_FILE"
echo "====================================" >> "$SUMMARY_FILE"
echo "" >> "$SUMMARY_FILE"
echo "Total Tests: $TOTAL_TESTS" >> "$SUMMARY_FILE"
echo "Passed: $TOTAL_PASSED" >> "$SUMMARY_FILE"
echo "Failed: $TOTAL_FAILED" >> "$SUMMARY_FILE"
echo "" >> "$SUMMARY_FILE"

if [ ${#FAILED_SERVICES[@]} -gt 0 ]; then
    echo "Failed Services:" >> "$SUMMARY_FILE"
    for SERVICE in "${FAILED_SERVICES[@]}"; do
        echo "  - $SERVICE" >> "$SUMMARY_FILE"
    done
fi

# Print summary
echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}             TEST SUMMARY${NC}"
echo -e "${YELLOW}========================================${NC}"
echo -e "Total Tests: ${TOTAL_TESTS}"
echo -e "Passed: ${GREEN}${TOTAL_PASSED}${NC}"
echo -e "Failed: ${RED}${TOTAL_FAILED}${NC}"
echo ""
echo -e "Results saved to: ${GREEN}$RUN_DIR${NC}"
echo ""

if [ ${#FAILED_SERVICES[@]} -gt 0 ]; then
    echo -e "${RED}Some services had test failures!${NC}"
    exit 1
else
    echo -e "${GREEN}All tests passed!${NC}"
    exit 0
fi
