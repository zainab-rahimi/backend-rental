package Openclassroom.com.rental.service;

import Openclassroom.com.rental.entity.User;
import Openclassroom.com.rental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
