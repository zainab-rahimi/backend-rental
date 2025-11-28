package openclassroom.com.rental.service;

import openclassroom.com.rental.entity.Rental;
import openclassroom.com.rental.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    @Transactional(readOnly = true)
    public List<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Rental> findRentalById(Integer id) {
        return rentalRepository.findById(id);
    }

    @Transactional
    public Rental saveRental(Rental rental) {
        return rentalRepository.save(rental);
    }

    @Transactional
    public void deleteRental(Integer id) {
        rentalRepository.deleteById(id);
    }
}
