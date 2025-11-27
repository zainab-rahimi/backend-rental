package openclassroom.com.rental.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponse {
    @JsonProperty("token")
    private final String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    // Keep backward compatibility with jwt property name
    @JsonProperty("jwt")
    public String getJwt() {
        return token;
    }
}
