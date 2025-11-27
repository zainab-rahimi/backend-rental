package openclassroom.com.rental.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) {
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Save file
            Path targetLocation = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Return the URL that can be used to access the file
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(uniqueFilename)
                    .toUriString();

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    /**
     * Download image from URL and store it locally
     * @param imageUrl The URL of the image to download
     * @return The local accessible URL
     */
    public String storeFileFromUrl(String imageUrl) {
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Extract file extension from URL
            String fileExtension = ".jpg"; // default
            String urlPath = imageUrl.toLowerCase();
            if (urlPath.contains(".png")) {
                fileExtension = ".png";
            } else if (urlPath.contains(".jpeg") || urlPath.contains(".jpg")) {
                fileExtension = ".jpg";
            } else if (urlPath.contains(".gif")) {
                fileExtension = ".gif";
            } else if (urlPath.contains(".webp")) {
                fileExtension = ".webp";
            }

            // Generate unique filename
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Download file from URL
            URL url = new URL(imageUrl);
            Path targetLocation = uploadPath.resolve(uniqueFilename);

            try (InputStream in = url.openStream()) {
                Files.copy(in, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            // Return the URL that can be used to access the file
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(uniqueFilename)
                    .toUriString();

        } catch (IOException ex) {
            throw new RuntimeException("Could not download and store file from URL: " + imageUrl + ". Error: " + ex.getMessage(), ex);
        }
    }
}
