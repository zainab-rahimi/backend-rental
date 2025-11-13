package Openclassroom.com.rental.repository;

import Openclassroom.com.rental.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
