#!/bin/bash

echo "Testing Pagination Functionality"
echo "================================"

# Wait for application to start
echo "Waiting for application to start..."
sleep 5

# Test 1: Get all currencies with pagination (first page)
echo -e "\n1. Testing paginated currencies (first page):"
curl -s -X GET "http://localhost:8080/api/v1/currencies/paginated?page=0&size=5" | jq '.data.metadata'

# Test 2: Get all currencies with pagination (second page)
echo -e "\n2. Testing paginated currencies (second page):"
curl -s -X GET "http://localhost:8080/api/v1/currencies/paginated?page=1&size=5" | jq '.data.metadata'

# Test 3: Search with pagination
echo -e "\n3. Testing search with pagination:"
curl -s -X GET "http://localhost:8080/api/v1/currencies/search/paginated?name=norvégienne&page=0&size=3" | jq '.data.metadata'

# Test 4: Test with default page size
echo -e "\n4. Testing with default page size:"
curl -s -X GET "http://localhost:8080/api/v1/currencies/paginated?page=0" | jq '.data.metadata'

echo -e "\nPagination tests completed!"

