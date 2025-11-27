package openclassroom.com.rental.controller;

import jakarta.validation.Valid;
import openclassroom.com.rental.dto.MessageRequest;
import openclassroom.com.rental.dto.MessageResponse;
import openclassroom.com.rental.entity.Message;
import openclassroom.com.rental.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    @Autowired
    public MessageController(MessageService messageService){
        this.messageService = messageService;
    }
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody MessageRequest request) {
       messageService.saveMessage(request);
       return ResponseEntity.ok(new MessageResponse("Message send with success"));
    }
    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> list = messageService.findAllMessages();
        return ResponseEntity.ok(list);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer id) {
        return messageService.findMessageById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Integer id) {
        return messageService.findMessageById(id)
                .map(msg -> {
                    messageService.deleteMessage(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
