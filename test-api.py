#!/usr/bin/env python3
"""
API Test Script for Instagram-like Posts Service
This script tests all CRUD operations on the posts API
"""

import urllib.request
import urllib.parse
import json
import sys
from typing import Dict, Any, Optional

BASE_URL = "http://localhost:8080"

# Colors for terminal output
GREEN = '\033[0;32m'
BLUE = '\033[0;34m'
YELLOW = '\033[1;33m'
NC = '\033[0m'  # No Color


def print_section(title: str):
    """Print a section header"""
    print(f"\n{YELLOW}>>> {title}{NC}")


def api_call(method: str, endpoint: str, data: Optional[Dict[Any, Any]] = None, description: str = "") -> Dict[Any, Any]:
    """Make an API call and return the JSON response"""
    url = f"{BASE_URL}{endpoint}"
    
    if description:
        print(f"{GREEN}{description}{NC}")
    
    try:
        # Prepare request
        json_data = None
        if data:
            json_data = json.dumps(data).encode('utf-8')
        
        # Create request
        req = urllib.request.Request(url, method=method)
        req.add_header('Content-Type', 'application/json')
        
        if json_data:
            req.add_header('Content-Length', str(len(json_data)))
        
        # Make the request
        with urllib.request.urlopen(req, json_data) as response:
            response_text = response.read().decode('utf-8')
            
            # Try to parse JSON, return empty dict if not JSON
            try:
                result = json.loads(response_text)
                print(json.dumps(result, indent=2))
                return result
            except json.JSONDecodeError:
                print(response_text)
                return {}
                
    except urllib.error.HTTPError as e:
        print(f"HTTP Error {e.code}: {e.reason}")
        error_body = e.read().decode('utf-8')
        if error_body:
            print(f"Response: {error_body}")
        return {}
    except urllib.error.URLError as e:
        print(f"URL Error: {e.reason}")
        print(f"Make sure the server is running on {BASE_URL}")
        return {}
    except Exception as e:
        print(f"Error: {e}")
        return {}


def main():
    print(f"{BLUE}========================================{NC}")
    print(f"{BLUE}API Test Script - Posts Service{NC}")
    print(f"{BLUE}========================================{NC}\n")
    
    # Step 1: Create Users
    print_section("Step 1: Creating Users")
    
    print("Creating User 1...")
    user1_response = api_call("POST", "/users/create", {
        "username": "user1",
        "email": "user1@example.com"
    }, "Creating User 1")
    user1_id = user1_response.get("userId", 1)
    
    print("Creating User 2...")
    user2_response = api_call("POST", "/users/create", {
        "username": "user2",
        "email": "user2@example.com"
    }, "Creating User 2")
    user2_id = user2_response.get("userId", 2)
    
    print(f"{GREEN}User 1 ID: {user1_id}{NC}")
    print(f"{GREEN}User 2 ID: {user2_id}{NC}\n")
    
    # Step 2: Create 3 posts for User 1
    print_section("Step 2: Creating 3 Posts for User 1")
    
    print("Creating Post 1 for User 1...")
    post1_response = api_call("POST", "/posts/create", {
        "userId": user1_id,
        "content": {
            "title": "User 1 Post 1",
            "description": "This is the first post from user 1",
            "mediaFiles": [
                {
                    "mediaUrl": "https://example.com/image1.jpg",
                    "mediaType": "image"
                }
            ]
        }
    }, "Creating Post 1")
    post1_id = post1_response.get("postId")
    
    print("Creating Post 2 for User 1...")
    post2_response = api_call("POST", "/posts/create", {
        "userId": user1_id,
        "content": {
            "title": "User 1 Post 2",
            "description": "This is the second post from user 1",
            "mediaFiles": [
                {
                    "mediaUrl": "https://example.com/image2.jpg",
                    "mediaType": "image"
                }
            ]
        }
    }, "Creating Post 2")
    post2_id = post2_response.get("postId")
    
    print("Creating Post 3 for User 1...")
    post3_response = api_call("POST", "/posts/create", {
        "userId": user1_id,
        "content": {
            "title": "User 1 Post 3",
            "description": "This is the third post from user 1"
        }
    }, "Creating Post 3")
    post3_id = post3_response.get("postId")
    
    # Step 3: Create 2 posts for User 2
    print_section("Step 3: Creating 2 Posts for User 2")
    
    print("Creating Post 1 for User 2...")
    post4_response = api_call("POST", "/posts/create", {
        "userId": user2_id,
        "content": {
            "title": "User 2 Post 1",
            "description": "This is the first post from user 2",
            "mediaFiles": [
                {
                    "mediaUrl": "https://example.com/image3.jpg",
                    "mediaType": "image"
                }
            ]
        }
    }, "Creating Post 1 for User 2")
    post4_id = post4_response.get("postId")
    
    print("Creating Post 2 for User 2...")
    post5_response = api_call("POST", "/posts/create", {
        "userId": user2_id,
        "content": {
            "title": "User 2 Post 2",
            "description": "This is the second post from user 2"
        }
    }, "Creating Post 2 for User 2")
    post5_id = post5_response.get("postId")
    
    print(f"{GREEN}User 1 Posts: {post1_id}, {post2_id}, {post3_id}{NC}")
    print(f"{GREEN}User 2 Posts: {post4_id}, {post5_id}{NC}\n")
    
    # Step 4: List all posts (timeline)
    print_section("Step 4: Listing All Posts (Timeline)")
    api_call("GET", "/posts?page=0&size=10", description="Getting Timeline (All Posts)")
    
    # Step 5: Get posts of User 1
    print_section("Step 5: Getting Posts of User 1")
    api_call("GET", f"/posts?userId={user1_id}&page=0&size=10", description="Getting Posts for User 1")
    
    # Step 6: Update post 1 of User 2
    print_section("Step 6: Updating Post 1 of User 2")
    api_call("PUT", f"/posts/{post4_id}", {
        "userId": user2_id,
        "content": {
            "title": "User 2 Post 1 - UPDATED",
            "description": "This post has been updated!",
            "mediaFiles": [
                {
                    "mediaUrl": "https://example.com/updated-image.jpg",
                    "mediaType": "image"
                }
            ]
        }
    }, f"Updating Post {post4_id} of User 2")
    
    # Step 7: Delete post 1 of User 2
    print_section("Step 7: Deleting Post 1 of User 2")
    api_call("DELETE", f"/posts/{post4_id}", description=f"Deleting Post {post4_id} of User 2")
    
    # Step 8: Get timeline after all operations
    print_section("Step 8: Getting Timeline After All Operations")
    api_call("GET", "/posts?page=0&size=10", description="Getting Final Timeline")
    
    print(f"\n{BLUE}========================================{NC}")
    print(f"{BLUE}Test Script Completed!{NC}")
    print(f"{BLUE}========================================{NC}")


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n\nScript interrupted by user.")
        sys.exit(1)
    except Exception as e:
        print(f"\n\nError: {e}")
        sys.exit(1)

