package Openclassroom.com.rental.repository;

import Openclassroom.com.rental.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message,Integer> {
}
