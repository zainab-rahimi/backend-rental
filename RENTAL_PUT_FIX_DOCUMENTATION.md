# Rental PUT Method - Picture Upload Fix

## Summary of Changes

This document explains the fixes made to enable picture uploads in the Rental PUT method and ensure picture URLs are properly saved in the database and accessible via URLs.

## Problems Fixed

1. **FileStorageService was not implementing file storage** - It was returning null
2. **PUT method didn't accept picture parameter** - Could not update rental pictures
3. **Uploaded files were not accessible via URL** - No static resource handler configured
4. **Picture URLs were not being saved to database** - Missing logic in controller

## Changes Made

### 1. FileStorageService.java - Implemented File Storage
**Location:** `/src/main/java/openclassroom/com/rental/service/FileStorageService.java`

**Changes:**
- Creates upload directory if it doesn't exist
- Generates unique filenames using UUID to avoid conflicts
- Saves files to the `uploads/` directory
- Returns a full URL (e.g., `http://localhost:8080/uploads/abc-123.jpg`)

**Key Code:**
```java
public String storeFile(MultipartFile file) {
    // Creates directory, saves file with unique name
    // Returns: http://localhost:8080/uploads/{unique-filename}
    return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/uploads/")
            .path(uniqueFilename)
            .toUriString();
}
```

### 2. WebConfig.java - Enable Static File Serving
**Location:** `/src/main/java/openclassroom/com/rental/config/WebConfig.java`

**Purpose:** 
- Maps the `/uploads/**` URL pattern to the physical `uploads/` directory
- Allows browsers/apps to access uploaded images via URL

**How it works:**
- Request: `GET http://localhost:8080/uploads/abc-123.jpg`
- Serves from: `{project-root}/uploads/abc-123.jpg`

### 3. SecurityConfig.java - Allow Public Access to Uploads
**Location:** `/src/main/java/openclassroom/com/rental/config/SecurityConfig.java`

**Changes:**
- Added `.requestMatchers("/uploads/**").permitAll()`
- Allows unauthenticated access to uploaded images
- Required so frontend/apps can display images without JWT token

### 4. RentalController.java - Updated PUT Method
**Location:** `/src/main/java/openclassroom/com/rental/controller/RentalController.java`

**Changes:**
- Added optional `picture` parameter: `@RequestParam(value = "picture", required = false) MultipartFile picture`
- Handles picture upload when provided
- Saves picture URL to database: `rental.setPictureUrl(pictureUrl)`
- Added proper error handling with try-catch

**Key Code:**
```java
@PutMapping("/{id}")
public ResponseEntity<?> updateRental(
        @PathVariable Integer id,
        @RequestParam("name") String name,
        @RequestParam("surface") BigDecimal surface,
        @RequestParam("price") BigDecimal price,
        @RequestParam(value = "picture", required = false) MultipartFile picture,
        @RequestParam("description") String description,
        Authentication authentication) {
    
    // ... existing code ...
    
    // Handle picture upload if provided
    if (picture != null && !picture.isEmpty()) {
        String pictureUrl = fileStorageService.storeFile(picture);
        rental.setPictureUrl(pictureUrl);
    }
    
    rental.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    rentalService.saveRental(rental);
    
    return ResponseEntity.ok(Map.of("message", "Rental updated!"));
}
```

## How It Works - Complete Flow

### Creating a Rental with Picture:
1. Frontend sends POST to `/api/rentals` with multipart form data
2. `FileStorageService.storeFile()` saves file and returns URL
3. URL saved to database in `rentals.picture` column
4. Example URL: `http://localhost:8080/uploads/a1b2c3d4-e5f6-7890.jpg`

### Updating a Rental with New Picture:
1. Frontend sends PUT to `/api/rentals/{id}` with optional picture
2. If picture provided: new file saved, new URL generated
3. Picture URL updated in database
4. Old picture remains on disk (could implement cleanup later)

### Accessing Picture:
1. GET request to picture URL (e.g., `/uploads/a1b2c3d4.jpg`)
2. `WebConfig` maps to physical file
3. `SecurityConfig` allows public access
4. File served to browser/app

## Testing

### Test Script Provided: `test-rental-put.sh`

**Usage:**
```bash
# Test without picture
./test-rental-put.sh 1 "your-jwt-token"

# Test with picture
./test-rental-put.sh 1 "your-jwt-token" /path/to/image.jpg
```

### Manual Testing with cURL:

**Update rental without picture:**
```bash
curl -X PUT "http://localhost:8080/api/rentals/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "name=Updated Name" \
  -F "surface=150.5" \
  -F "price=2500" \
  -F "description=Updated description"
```

**Update rental with picture:**
```bash
curl -X PUT "http://localhost:8080/api/rentals/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "name=Updated Name" \
  -F "surface=150.5" \
  -F "price=2500" \
  -F "picture=@/path/to/image.jpg" \
  -F "description=Updated with new picture"
```

**Verify the update:**
```bash
curl -X GET "http://localhost:8080/api/rentals/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Access the picture directly:**
```bash
# The picture URL from the response above, e.g.:
curl http://localhost:8080/uploads/a1b2c3d4-e5f6-7890.jpg
```

## Database Schema

The `rentals` table has a `picture` column that stores the full URL:

```sql
CREATE TABLE rentals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    surface DECIMAL(10,2),
    price DECIMAL(10,2),
    picture VARCHAR(500),  -- Stores full URL
    description TEXT,
    owner_id INT,
    create_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

**Example stored value:**
```
http://localhost:8080/uploads/a1b2c3d4-e5f6-7890-abcd-1234567890ab.jpg
```

## Configuration Required

Ensure `application.properties` has:
```properties
# File upload settings
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.upload-dir=uploads/
```

## Important Notes

1. **uploads/ directory** will be created automatically in project root
2. **Picture parameter is optional** in PUT - can update without changing picture
3. **Old pictures are not deleted** - consider implementing cleanup
4. **UUIDs prevent filename conflicts** - multiple uploads of "image.jpg" won't collide
5. **Full URLs stored in database** - easy for frontend to use directly

## Debugging Tips

If pictures aren't working:

1. **Check uploads directory exists:** `ls -la uploads/`
2. **Check file was saved:** `ls -la uploads/` after upload
3. **Test direct access:** Visit `http://localhost:8080/uploads/filename.jpg` in browser
4. **Check database:** Verify picture URL is saved in rentals table
5. **Check logs:** Look for any FileStorageService exceptions
6. **Check permissions:** Ensure application can write to uploads/ directory

## Frontend Integration

Frontend should:
1. Use `multipart/form-data` content type
2. Include picture as form field (optional)
3. Display picture using URL from response
4. No authentication needed to display images (public access)

**Example Frontend Code:**
```javascript
// Update rental with picture
const formData = new FormData();
formData.append('name', 'Updated Name');
formData.append('surface', 150.5);
formData.append('price', 2500);
formData.append('description', 'Updated description');
if (pictureFile) {
    formData.append('picture', pictureFile);
}

fetch(`http://localhost:8080/api/rentals/${id}`, {
    method: 'PUT',
    headers: {
        'Authorization': `Bearer ${token}`
    },
    body: formData
});
```

## Success!

Your rental PUT method now:
- ✅ Accepts picture uploads
- ✅ Saves pictures to disk
- ✅ Generates accessible URLs
- ✅ Saves URLs to database
- ✅ Serves pictures publicly
- ✅ Handles updates with or without pictures

