package openclassroom.com.rental.repository;

import openclassroom.com.rental.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Integer> {

}
