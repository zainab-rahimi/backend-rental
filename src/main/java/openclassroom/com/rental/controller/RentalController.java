package openclassroom.com.rental.controller;
import openclassroom.com.rental.dto.rental.ListRentalResponse;
import openclassroom.com.rental.dto.rental.RentalResponse;
import openclassroom.com.rental.entity.Rental;
import openclassroom.com.rental.entity.User;
import openclassroom.com.rental.exception.BadRequestException;
import openclassroom.com.rental.exception.ResourceNotFoundException;
import openclassroom.com.rental.exception.UnauthorizedException;
import openclassroom.com.rental.service.FileStorageService;
import openclassroom.com.rental.service.RentalService;
import openclassroom.com.rental.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    private final RentalService rentalService;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    public RentalController(
            RentalService rentalService,
            UserService userService,
            FileStorageService fileStorageService) {
        this.rentalService = rentalService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }
    @GetMapping
    public ResponseEntity<ListRentalResponse> getAllRentals(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException("Not authenticated");
        }
        List<Rental> rentals = rentalService.findAllRentals();
        List<RentalResponse> rentalResponses = rentals.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ListRentalResponse(rentalResponses));
    }
    private RentalResponse convertToResponse(Rental rental) {
        RentalResponse response = new RentalResponse();
        response.setId(rental.getId());
        response.setName(rental.getName());
        response.setSurface(rental.getSurface());
        response.setPrice(rental.getPrice());
        response.setDescription(rental.getDescription());
        response.setPicture(rental.getPictureUrl());
        response.setOwner_id(rental.getOwner().getId());
        response.setCreatedAt(rental.getCreatedAt());
        response.setUpdatedAt(rental.getUpdatedAt());
        return response;
    }
    @GetMapping("/{id}")
    public ResponseEntity<RentalResponse> getRentalById(@PathVariable Integer id, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException("Not authenticated");
        }
        Rental rental = rentalService.findRentalById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found with id: " + id));
        RentalResponse response = convertToResponse(rental);
        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<Map<String, String>> createRental(
            @RequestParam("name") String name,
            @RequestParam("surface") BigDecimal surface,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "picture") MultipartFile picture,
            @RequestParam("description") String description,
            Authentication authentication) {
        if (picture == null || picture.isEmpty()) {
            throw new BadRequestException("Picture is required");
        }
        // Get the current user from authentication
        String email = authentication.getName();
        User owner = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // Store the file and get the url
        String finalPictureUrl = fileStorageService.storeFile(picture);
        Rental rental = new Rental();
        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setPictureUrl(finalPictureUrl);
        rental.setDescription(description);
        rental.setOwner(owner);
        rental.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        rental.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        rentalService.saveRental(rental);
        return ResponseEntity.ok(Map.of("message", "Rental created successfully"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateRental(
            @PathVariable Integer id,
            @RequestParam("name") String name,
            @RequestParam("surface") BigDecimal surface,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "picture", required = false) MultipartFile picture,
            @RequestParam("description") String description,
            Authentication authentication) {
        // Find rental
        Rental rental = rentalService.findRentalById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found with id: " + id));
        // Verify ownership
        String email = authentication.getName();
        if (!rental.getOwner().getEmail().equals(email)) {
            throw new UnauthorizedException("You don't have permission to update this rental");
        }
        // Update rental fields
        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setDescription(description);
        // Handle picture upload if provided
        if (picture != null && !picture.isEmpty()) {
            String newPictureUrl = fileStorageService.storeFile(picture);
            rental.setPictureUrl(newPictureUrl);
        }
        rental.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        rentalService.saveRental(rental);
        return ResponseEntity.ok(Map.of("message", "Rental updated!"));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable Integer id) {
        return rentalService.findRentalById(id)
                .map(msg -> {
                    rentalService.deleteRental(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
