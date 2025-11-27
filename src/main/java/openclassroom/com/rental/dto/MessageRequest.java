package openclassroom.com.rental.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public class MessageRequest {
    @NotNull(message = "Rental ID is required")
    private Integer rental_id;
    @NotNull(message = "User ID is required")
    private Integer user_id;
    @NotBlank(message = "Message is required")
    private String message;
    public Integer getRental_id() {
        return rental_id;
    }
    public void setRental_id(Integer rental_id) {
        this.rental_id = rental_id;
    }
    public Integer getUser_id() {
        return user_id;
    }
    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
