package openclassroom.com.rental.service;

import openclassroom.com.rental.entity.User;
import openclassroom.com.rental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    public Optional<User> findUserById(Integer id) {
        return userRepository.findById(id);
    }
    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        // Add pre-save business logic like password encoding here
        return userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

}
