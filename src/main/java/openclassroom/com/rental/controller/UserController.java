package openclassroom.com.rental.controller;

import openclassroom.com.rental.entity.User;
import openclassroom.com.rental.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }




}
