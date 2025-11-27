package openclassroom.com.rental.controller;

import openclassroom.com.rental.dto.rental.ListRentalResponse;
import openclassroom.com.rental.dto.rental.RentalResponse;
import openclassroom.com.rental.entity.Rental;
import openclassroom.com.rental.entity.User;
import openclassroom.com.rental.service.FileStorageService;
import openclassroom.com.rental.service.RentalService;
import openclassroom.com.rental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    @Autowired
    private RentalService rentalService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<ListRentalResponse> getAllRentals() {
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
    public ResponseEntity<?> getRentalById(@PathVariable Integer id) {
        Optional<Rental> rental = rentalService.findRentalById(id);

        if (rental.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RentalResponse response = convertToResponse(rental.get());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createRental(
            @RequestParam("name") String name,
            @RequestParam("surface") BigDecimal surface,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "picture") MultipartFile picture,
            @RequestParam("description") String description,
            Authentication authentication) {

        try {
            if ( picture == null || picture.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", " Picture is needed"));
            }
            // 1. Get the current user from authentication
            String email = authentication.getName();
            User owner = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // store the file and get the url
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

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create rental: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRental(@PathVariable Integer id,
                                          @RequestParam("name") String name,
                                          @RequestParam("surface") BigDecimal surface,
                                          @RequestParam("price") BigDecimal price,
                                          @RequestParam(value = "picture", required = false) MultipartFile picture,
                                          @RequestParam("description") String description,
                                          Authentication authentication
    ) {
        try {
            // Find rental
            Rental rental = rentalService.findRentalById(id)
                    .orElseThrow(() -> new RuntimeException("Rental not found"));

            // Verify ownership
            String email = authentication.getName();
            if (!rental.getOwner().getEmail().equals(email)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You don't have permission to update this rental"));
            }

            // Update rental fields
            rental.setName(name);
            rental.setSurface(surface);
            rental.setPrice(price);
            rental.setDescription(description);

            // Handle picture upload or URL
            if (picture != null && !picture.isEmpty()) {
                // Upload file
                String newPictureUrl = fileStorageService.storeFile(picture);
                rental.setPictureUrl(newPictureUrl);
            }

            rental.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            rentalService.saveRental(rental);

            return ResponseEntity.ok(Map.of("message", "Rental updated!"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update rental: " + e.getMessage()));
        }
    }
}
