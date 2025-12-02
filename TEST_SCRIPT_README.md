# API Test Script

This directory contains test scripts to verify all CRUD operations on the Posts API.

## Prerequisites

1. Make sure your Spring Boot application is running on `http://localhost:8080`
2. Python 3 is installed (for `test-api.py`) or Bash (for `test-api.sh`)

## Running the Test Script

### Option 1: Python Script (Recommended)

```bash
python3 test-api.py
```

or

```bash
./test-api.py
```

### Option 2: Bash Script

```bash
./test-api.sh
```

**Note:** The bash script requires Python 3 for JSON parsing. If Python is not available, use the Python script instead.

## What the Script Does

The test script performs the following operations in sequence:

1. **Creates Users**: Creates User 1 and User 2
2. **Creates Posts for User 1**: Creates 3 posts with different content
3. **Creates Posts for User 2**: Creates 2 posts with different content
4. **Lists All Posts**: Retrieves the timeline (all posts, newest first)
5. **Gets User 1 Posts**: Retrieves all posts belonging to User 1
6. **Updates Post**: Updates the first post of User 2
7. **Deletes Post**: Deletes the first post of User 2
8. **Final Timeline**: Retrieves the timeline again to see the changes

## Expected Output

The script will print:
- Colored section headers for each step
- JSON responses from each API call
- Post IDs and User IDs for reference
- Final timeline showing the state after all operations

## Troubleshooting

- **Connection Error**: Make sure the Spring Boot application is running on port 8080
- **404 Errors**: Ensure all endpoints are properly configured
- **500 Errors**: Check the application logs for server-side errors

## API Endpoints Used

- `POST /users/create` - Create a user
- `POST /posts/create` - Create a post
- `GET /posts?page=0&size=10` - Get timeline (all posts)
- `GET /posts?userId={id}&page=0&size=10` - Get posts by user
- `PUT /posts/{id}` - Update a post
- `DELETE /posts/{id}` - Delete a post

