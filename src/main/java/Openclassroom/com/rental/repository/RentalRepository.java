package Openclassroom.com.rental.repository;

import Openclassroom.com.rental.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Integer> {
}
