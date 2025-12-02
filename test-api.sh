#!/bin/bash

# API Test Script for Instagram-like Posts Service
# This script tests all CRUD operations on the posts API

BASE_URL="http://localhost:8080"
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}API Test Script - Posts Service${NC}"
echo -e "${BLUE}========================================${NC}\n"

# Function to print section headers
print_section() {
    echo -e "\n${YELLOW}>>> $1${NC}"
}

# Function to make API calls and pretty print JSON
api_call() {
    local method=$1
    local url=$2
    local data=$3
    local description=$4
    
    echo -e "${GREEN}$description${NC}"
    if [ -z "$data" ]; then
        response=$(curl -s -X $method "$url" -H "Content-Type: application/json")
    else
        response=$(curl -s -X $method "$url" -H "Content-Type: application/json" -d "$data")
    fi
    
    echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"
    echo ""
    
    echo "$response"
}

# Step 1: Create Users
print_section "Step 1: Creating Users"

echo "Creating User 1..."
USER1_RESPONSE=$(api_call "POST" "$BASE_URL/users/create" '{"username":"user1","email":"user1@example.com"}' "Creating User 1")
USER1_ID=$(echo $USER1_RESPONSE | python3 -c "import sys, json; print(json.load(sys.stdin)['userId'])" 2>/dev/null || echo "1")

echo "Creating User 2..."
USER2_RESPONSE=$(api_call "POST" "$BASE_URL/users/create" '{"username":"user2","email":"user2@example.com"}' "Creating User 2")
USER2_ID=$(echo $USER2_RESPONSE | python3 -c "import sys, json; print(json.load(sys.stdin)['userId'])" 2>/dev/null || echo "2")

echo -e "${GREEN}User 1 ID: $USER1_ID${NC}"
echo -e "${GREEN}User 2 ID: $USER2_ID${NC}\n"

# Step 2: Create 3 posts for User 1
print_section "Step 2: Creating 3 Posts for User 1"

echo "Creating Post 1 for User 1..."
POST1_RESPONSE=$(api_call "POST" "$BASE_URL/posts/create" "{\"userId\":$USER1_ID,\"content\":{\"title\":\"User 1 Post 1\",\"description\":\"This is the first post from user 1\",\"mediaFiles\":[{\"mediaUrl\":\"https://example.com/image1.jpg\",\"mediaType\":\"image\"}]}}" "Creating Post 1")
POST1_ID=$(echo $POST1_RESPONSE | python3 -c "import sys, json; print(json.load(sys.stdin)['postId'])" 2>/dev/null)

echo "Creating Post 2 for User 1..."
POST2_RESPONSE=$(api_call "POST" "$BASE_URL/posts/create" "{\"userId\":$USER1_ID,\"content\":{\"title\":\"User 1 Post 2\",\"description\":\"This is the second post from user 1\",\"mediaFiles\":[{\"mediaUrl\":\"https://example.com/image2.jpg\",\"mediaType\":\"image\"}]}}" "Creating Post 2")
POST2_ID=$(echo $POST2_RESPONSE | python3 -c "import sys, json; print(json.load(sys.stdin)['postId'])" 2>/dev/null)

echo "Creating Post 3 for User 1..."
POST3_RESPONSE=$(api_call "POST" "$BASE_URL/posts/create" "{\"userId\":$USER1_ID,\"content\":{\"title\":\"User 1 Post 3\",\"description\":\"This is the third post from user 1\"}}" "Creating Post 3")
POST3_ID=$(echo $POST3_RESPONSE | python3 -c "import sys, json; print(json.load(sys.stdin)['postId'])" 2>/dev/null)

# Step 3: Create 2 posts for User 2
print_section "Step 3: Creating 2 Posts for User 2"

echo "Creating Post 1 for User 2..."
POST4_RESPONSE=$(api_call "POST" "$BASE_URL/posts/create" "{\"userId\":$USER2_ID,\"content\":{\"title\":\"User 2 Post 1\",\"description\":\"This is the first post from user 2\",\"mediaFiles\":[{\"mediaUrl\":\"https://example.com/image3.jpg\",\"mediaType\":\"image\"}]}}" "Creating Post 1 for User 2")
POST4_ID=$(echo $POST4_RESPONSE | python3 -c "import sys, json; print(json.load(sys.stdin)['postId'])" 2>/dev/null)

echo "Creating Post 2 for User 2..."
POST5_RESPONSE=$(api_call "POST" "$BASE_URL/posts/create" "{\"userId\":$USER2_ID,\"content\":{\"title\":\"User 2 Post 2\",\"description\":\"This is the second post from user 2\"}}" "Creating Post 2 for User 2")
POST5_ID=$(echo $POST5_RESPONSE | python3 -c "import sys, json; print(json.load(sys.stdin)['postId'])" 2>/dev/null)

echo -e "${GREEN}User 1 Posts: $POST1_ID, $POST2_ID, $POST3_ID${NC}"
echo -e "${GREEN}User 2 Posts: $POST4_ID, $POST5_ID${NC}\n"

# Step 4: List all posts (timeline)
print_section "Step 4: Listing All Posts (Timeline)"
api_call "GET" "$BASE_URL/posts?page=0&size=10" "" "Getting Timeline (All Posts)"

# Step 5: Get posts of User 1
print_section "Step 5: Getting Posts of User 1"
api_call "GET" "$BASE_URL/posts?userId=$USER1_ID&page=0&size=10" "" "Getting Posts for User 1"

# Step 6: Update post 1 of User 2
print_section "Step 6: Updating Post 1 of User 2"
api_call "PUT" "$BASE_URL/posts/$POST4_ID" "{\"userId\":$USER2_ID,\"content\":{\"title\":\"User 2 Post 1 - UPDATED\",\"description\":\"This post has been updated!\",\"mediaFiles\":[{\"mediaUrl\":\"https://example.com/updated-image.jpg\",\"mediaType\":\"image\"}]}}" "Updating Post $POST4_ID of User 2"

# Step 7: Delete post 1 of User 2
print_section "Step 7: Deleting Post 1 of User 2"
api_call "DELETE" "$BASE_URL/posts/$POST4_ID" "" "Deleting Post $POST4_ID of User 2"

# Step 8: Get timeline after all operations
print_section "Step 8: Getting Timeline After All Operations"
api_call "GET" "$BASE_URL/posts?page=0&size=10" "" "Getting Final Timeline"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Test Script Completed!${NC}"
echo -e "${BLUE}========================================${NC}"

