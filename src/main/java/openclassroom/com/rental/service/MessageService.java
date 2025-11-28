package openclassroom.com.rental.service;
import openclassroom.com.rental.dto.MessageRequest;
import openclassroom.com.rental.entity.Message;
import openclassroom.com.rental.entity.Rental;
import openclassroom.com.rental.entity.User;
import openclassroom.com.rental.exception.ResourceNotFoundException;
import openclassroom.com.rental.repository.MessageRepository;
import openclassroom.com.rental.repository.RentalRepository;
import openclassroom.com.rental.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    public MessageService(
            MessageRepository messageRepository,
            RentalRepository rentalRepository,
            UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Message> findAllMessages() {
        return messageRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Message> findMessageById(Integer id) {
        return messageRepository.findById(id);
    }

    @Transactional
    public Message saveMessage(MessageRequest request) {
        // Fetch the rental and user entities
        Rental rental = rentalRepository.findById(request.getRental_id())
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found with id: " + request.getRental_id()));

        User user = userRepository.findById(request.getUser_id())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUser_id()));

        // Create the message entity
        Message message = new Message();
        message.setRental(rental);
        message.setUser(user);
        message.setMessage(request.getMessage());

        // Set timestamps
        Timestamp now = new Timestamp(System.currentTimeMillis());
        message.setCreatedAt(now);
        message.setUpdatedAt(now);

        return messageRepository.save(message);
    }

    @Transactional
    public void deleteMessage(Integer id) {
        messageRepository.deleteById(id);
    }

}
